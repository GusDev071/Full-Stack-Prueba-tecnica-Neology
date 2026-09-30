package com.gustavo.prueba.tecnica.neology.pruebatecnica.service.impl;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.InformePagosResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.PagoResidenteResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Residente;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.ResidenteRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.ResidenteService;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaContext;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResidenteServiceImpl implements ResidenteService {

    private final ResidenteRepository residenteRepository;
    private final TarifaContext tarifaContext;

    @Override
    @Transactional(readOnly = true)
    public InformePagosResponse generarInformePagos() {
        TarifaStrategy strategy = tarifaContext.getStrategy(TipoVehiculo.RESIDENTE);
        List<Residente> residentes = residenteRepository.findAll();

        List<PagoResidenteResponse> pagos = residentes.stream()
                .map(r -> {
                    long minutos = r.getTiempoAcumulado() != null ? r.getTiempoAcumulado() : 0L;
                    BigDecimal totalPagar = strategy.calcularImporteMensual(minutos);
                    return PagoResidenteResponse.builder()
                            .placa(r.getPlaca())
                            .tiempoAcumuladoMinutos(minutos)
                            .tarifaPorMinuto(strategy.getTarifaPorMinuto())
                            .totalPagar(totalPagar)
                            .build();
                })
                .collect(Collectors.toList());

        long totalMinutos = pagos.stream()
                .mapToLong(PagoResidenteResponse::getTiempoAcumuladoMinutos)
                .sum();

        BigDecimal totalCobro = pagos.stream()
                .map(PagoResidenteResponse::getTotalPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return InformePagosResponse.builder()
                .fechaGeneracion(LocalDateTime.now())
                .totalResidentes(pagos.size())
                .totalMinutosAcumulados(totalMinutos)
                .totalCobro(totalCobro)
                .residentes(pagos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResidenteResponse> listarPagosResidentes() {
        return generarInformePagos().getResidentes();
    }
}
