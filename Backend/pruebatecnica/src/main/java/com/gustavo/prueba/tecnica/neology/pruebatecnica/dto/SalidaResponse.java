package com.gustavo.prueba.tecnica.neology.pruebatecnica.dto;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
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
public class SalidaResponse {

    private Long idEstancia;
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;
    private Long minutos;
    private BigDecimal tarifaPorMinuto;
    private BigDecimal importeCobrado;
    private Long tiempoAcumuladoTotal;
    private String mensaje;
}
