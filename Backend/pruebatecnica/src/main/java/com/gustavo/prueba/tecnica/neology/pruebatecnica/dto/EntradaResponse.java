package com.gustavo.prueba.tecnica.neology.pruebatecnica.dto;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntradaResponse {

    private Long idEstancia;
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private LocalDateTime fechaHoraEntrada;
    private String mensaje;
}
