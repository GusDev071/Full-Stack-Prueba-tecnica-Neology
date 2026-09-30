package com.gustavo.prueba.tecnica.neology.pruebatecnica.service.impl;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EstanciaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.VehiculoDetalleResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.VehiculoRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.VehiculoResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.exception.RecursoNoEncontradoException;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.exception.ReglaNegocioException;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Estancia;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Residente;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Vehiculo;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.EstanciaRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.ResidenteRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.VehiculoRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ResidenteRepository residenteRepository;
    private final EstanciaRepository estanciaRepository;

    @Override
    @Transactional
    public VehiculoResponse registrarOficial(VehiculoRequest request) {
        String placa = limpiarPlaca(request.getPlaca());
        validarNoRegistradoConOtroTipo(placa, TipoVehiculo.OFICIAL);

        Vehiculo vehiculo = vehiculoRepository.findById(placa)
                .orElseGet(() -> vehiculoRepository.save(
                        Vehiculo.builder()
                                .placa(placa)
                                .tipo(TipoVehiculo.OFICIAL)
                                .fechaAlta(LocalDateTime.now())
                                .build()
                ));

        return mapearVehiculoResponse(vehiculo);
    }

    @Override
    @Transactional
    public VehiculoResponse registrarResidente(VehiculoRequest request) {
        String placa = limpiarPlaca(request.getPlaca());
        validarNoRegistradoConOtroTipo(placa, TipoVehiculo.RESIDENTE);

        Vehiculo vehiculo = vehiculoRepository.findById(placa)
                .orElseGet(() -> vehiculoRepository.save(
                        Vehiculo.builder()
                                .placa(placa)
                                .tipo(TipoVehiculo.RESIDENTE)
                                .fechaAlta(LocalDateTime.now())
                                .build()
                ));

        if (!residenteRepository.existsById(placa)) {
            residenteRepository.save(
                    Residente.builder()
                            .placa(placa)
                            .tiempoAcumulado(0L)
                            .build()
            );
        }

        return mapearVehiculoResponse(vehiculo);
    }

    @Override
    @Transactional
    public VehiculoResponse registrarNoResidente(VehiculoRequest request) {
        String placa = limpiarPlaca(request.getPlaca());
        validarNoRegistradoConOtroTipo(placa, TipoVehiculo.NO_RESIDENTE);

        Vehiculo vehiculo = vehiculoRepository.findById(placa)
                .orElseGet(() -> vehiculoRepository.save(
                        Vehiculo.builder()
                                .placa(placa)
                                .tipo(TipoVehiculo.NO_RESIDENTE)
                                .fechaAlta(LocalDateTime.now())
                                .build()
                ));

        return mapearVehiculoResponse(vehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoResponse> listarTodos() {
        return vehiculoRepository.findAll().stream()
                .map(this::mapearVehiculoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoDetalleResponse obtenerDetalle(String placa) {
        String placaLimpia = limpiarPlaca(placa);
        Vehiculo vehiculo = vehiculoRepository.findById(placaLimpia)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado con placa: " + placaLimpia));

        List<Estancia> estancias = estanciaRepository.findByPlacaOrderByFechaHoraEntradaDesc(placaLimpia);
        boolean estanciaActiva = estancias.stream().anyMatch(Estancia::estaActiva);

        Long tiempoAcumulado = null;
        if (vehiculo.getTipo() == TipoVehiculo.RESIDENTE) {
            tiempoAcumulado = residenteRepository.findById(placaLimpia)
                    .map(Residente::getTiempoAcumulado)
                    .orElse(0L);
        }

        List<EstanciaResponse> estanciaResponses = estancias.stream()
                .map(this::mapearEstanciaResponse)
                .collect(Collectors.toList());

        return VehiculoDetalleResponse.builder()
                .placa(vehiculo.getPlaca())
                .tipo(vehiculo.getTipo())
                .fechaAlta(vehiculo.getFechaAlta())
                .tiempoAcumulado(tiempoAcumulado)
                .estanciaActiva(estanciaActiva)
                .totalEstancias(estancias.size())
                .estancias(estanciaResponses)
                .build();
    }

    @Override
    @Transactional
    public Vehiculo obtenerOcrearVehiculo(String placa) {
        String placaLimpia = limpiarPlaca(placa);
        return vehiculoRepository.findById(placaLimpia)
                .orElseGet(() -> vehiculoRepository.save(
                        Vehiculo.builder()
                                .placa(placaLimpia)
                                .tipo(TipoVehiculo.NO_RESIDENTE)
                                .fechaAlta(LocalDateTime.now())
                                .build()
                ));
    }

    private void validarNoRegistradoConOtroTipo(String placa, TipoVehiculo tipoNuevo) {
        Optional<Vehiculo> existente = vehiculoRepository.findById(placa);
        if (existente.isPresent() && existente.get().getTipo() != tipoNuevo) {
            throw new ReglaNegocioException(
                    String.format("El vehículo con placa %s ya se encuentra registrado como %s",
                            placa, existente.get().getTipo())
            );
        }
    }

    private String limpiarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new ReglaNegocioException("La placa no puede ser nula o vacía");
        }
        return placa.trim().toUpperCase();
    }

    private VehiculoResponse mapearVehiculoResponse(Vehiculo vehiculo) {
        Long tiempoAcumulado = null;
        if (vehiculo.getTipo() == TipoVehiculo.RESIDENTE) {
            tiempoAcumulado = residenteRepository.findById(vehiculo.getPlaca())
                    .map(Residente::getTiempoAcumulado)
                    .orElse(0L);
        }

        boolean activa = estanciaRepository.findByPlacaAndFechaHoraSalidaIsNull(vehiculo.getPlaca()).isPresent();

        return VehiculoResponse.builder()
                .placa(vehiculo.getPlaca())
                .tipo(vehiculo.getTipo())
                .fechaAlta(vehiculo.getFechaAlta())
                .tiempoAcumulado(tiempoAcumulado)
                .estanciaActiva(activa)
                .build();
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
