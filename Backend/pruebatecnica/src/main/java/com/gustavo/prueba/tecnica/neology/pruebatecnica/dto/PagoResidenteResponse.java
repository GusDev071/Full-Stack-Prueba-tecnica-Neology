package com.gustavo.prueba.tecnica.neology.pruebatecnica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResidenteResponse {

    private String placa;
    private Long tiempoAcumuladoMinutos;
    private BigDecimal tarifaPorMinuto;
    private BigDecimal totalPagar;
}
