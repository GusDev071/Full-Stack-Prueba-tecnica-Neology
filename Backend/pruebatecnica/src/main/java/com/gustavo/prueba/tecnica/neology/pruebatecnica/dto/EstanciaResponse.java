package com.gustavo.prueba.tecnica.neology.pruebatecnica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstanciaResponse {

    private Long id;
    private String placa;
    private LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;
    private Long minutos;
    private BigDecimal importe;
    private boolean activa;
}
