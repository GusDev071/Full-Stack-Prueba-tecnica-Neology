package com.gustavo.prueba.tecnica.neology.pruebatecnica.service.impl;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EntradaRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EntradaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EstanciaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.SalidaRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.SalidaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.exception.RecursoNoEncontradoException;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.exception.ReglaNegocioException;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Estancia;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Residente;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Vehiculo;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.EstanciaRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.ResidenteRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.VehiculoRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.EstanciaService;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.VehiculoService;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaContext;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstanciaServiceImpl implements EstanciaService {

    private final EstanciaRepository estanciaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ResidenteRepository residenteRepository;
    private final VehiculoService vehiculoService;
    private final TarifaContext tarifaContext;

    @Override
    @Transactional
    public EntradaResponse registrarEntrada(EntradaRequest request) {
        String placa = limpiarPlaca(request.getPlaca());

        // Asegurar que el vehículo existe en catálogo (si no existe, entra como NO_RESIDENTE)
        Vehiculo vehiculo = vehiculoService.obtenerOcrearVehiculo(placa);

        // Validar que no tenga una estancia activa
        estanciaRepository.findByPlacaAndFechaHoraSalidaIsNull(placa).ifPresent(e -> {
            throw new ReglaNegocioException(
                    String.format("El vehículo con placa %s ya cuenta con una estancia activa desde %s",
                            placa, e.getFechaHoraEntrada())
            );
        });

        LocalDateTime fechaEntrada = request.getFechaHora() != null ? request.getFechaHora() : LocalDateTime.now();

        Estancia estancia = Estancia.builder()
                .placa(placa)
                .fechaHoraEntrada(fechaEntrada)
                .build();

        estancia = estanciaRepository.save(estancia);

        return EntradaResponse.builder()
                .idEstancia(estancia.getId())
                .placa(placa)
                .tipoVehiculo(vehiculo.getTipo())
                .fechaHoraEntrada(estancia.getFechaHoraEntrada())
                .mensaje("Entrada registrada con éxito para vehículo tipo " + vehiculo.getTipo())
                .build();
    }

    @Override
    @Transactional
    public SalidaResponse registrarSalida(SalidaRequest request) {
        String placa = limpiarPlaca(request.getPlaca());

        Estancia estancia = estanciaRepository.findByPlacaAndFechaHoraSalidaIsNull(placa)
                .orElseThrow(() -> new ReglaNegocioException(
                        "No se encontró ninguna estancia activa para el vehículo con placa: " + placa
                ));

        LocalDateTime fechaSalida = request.getFechaHora() != null ? request.getFechaHora() : LocalDateTime.now();

        if (fechaSalida.isBefore(estancia.getFechaHoraEntrada())) {
            throw new ReglaNegocioException("La fecha de salida no puede ser anterior a la fecha de entrada");
        }

        long minutos = Duration.between(estancia.getFechaHoraEntrada(), fechaSalida).toMinutes();
        if (minutos <= 0) {
            minutos = 1; // Tarifa mínima por fracción de minuto
        }

        Vehiculo vehiculo = vehiculoRepository.findById(placa)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado con placa: " + placa));

        TarifaStrategy strategy = tarifaContext.getStrategy(vehiculo.getTipo());
        BigDecimal importe = strategy.calcularImporteSalida(minutos);

        Long tiempoAcumuladoTotal = null;
        String mensaje;

        if (strategy.acumulaTiempoFinDeMes()) {
            Residente residente = residenteRepository.findById(placa)
                    .orElseGet(() -> Residente.builder()
                            .placa(placa)
                            .tiempoAcumulado(0L)
                            .build());

            residente.acumularMinutos(minutos);
            residenteRepository.save(residente);
            tiempoAcumuladoTotal = residente.getTiempoAcumulado();
            mensaje = String.format("Salida registrada. Se acumularon %d minutos. Total acumulado en el mes: %d min.",
                    minutos, tiempoAcumuladoTotal);
        } else if (importe.compareTo(BigDecimal.ZERO) == 0) {
            mensaje = "Salida de vehículo oficial registrada. No aplica cobro.";
        } else {
            mensaje = String.format("Salida registrada. Duración: %d min. Importe cobrado: $%s", minutos, importe);
        }

        estancia.setFechaHoraSalida(fechaSalida);
        estancia.setMinutos(minutos);
        estancia.setImporte(importe);
        estanciaRepository.save(estancia);

        return SalidaResponse.builder()
                .idEstancia(estancia.getId())
                .placa(placa)
                .tipoVehiculo(vehiculo.getTipo())
                .fechaHoraEntrada(estancia.getFechaHoraEntrada())
                .fechaHoraSalida(fechaSalida)
                .minutos(minutos)
                .tarifaPorMinuto(strategy.getTarifaPorMinuto())
                .importeCobrado(importe)
                .tiempoAcumuladoTotal(tiempoAcumuladoTotal)
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstanciaResponse> listarEstancias() {
        return estanciaRepository.findAllByOrderByFechaHoraEntradaDesc().stream()
                .map(this::mapearEstanciaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstanciaResponse> listarEstanciasActivas() {
        return estanciaRepository.findByFechaHoraSalidaIsNull().stream()
                .map(this::mapearEstanciaResponse)
                .collect(Collectors.toList());
    }

    private String limpiarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new ReglaNegocioException("La placa no puede ser nula o vacía");
        }
        return placa.trim().toUpperCase();
    }

    private EstanciaResponse mapearEstanciaResponse(Estancia estancia) {
        return EstanciaResponse.builder()
                .id(estancia.getId())
                .placa(estancia.getPlaca())
                .fechaHoraEntrada(estancia.getFechaHoraEntrada())
                .fechaHoraSalida(estancia.getFechaHoraSalida())
                .minutos(estancia.getMinutos())
                .importe(estancia.getImporte())
                .activa(estancia.estaActiva())
                .build();
    }
}
