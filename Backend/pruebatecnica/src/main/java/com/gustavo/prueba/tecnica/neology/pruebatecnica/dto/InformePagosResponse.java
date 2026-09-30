package com.gustavo.prueba.tecnica.neology.pruebatecnica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformePagosResponse {

    private LocalDateTime fechaGeneracion;
    private int totalResidentes;
    private Long totalMinutosAcumulados;
    private BigDecimal totalCobro;
    private List<PagoResidenteResponse> residentes;
}
