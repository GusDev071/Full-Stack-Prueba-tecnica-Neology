package com.gustavo.prueba.tecnica.neology.pruebatecnica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenMesResponse {

    private String mensaje;
    private int residentesReiniciados;
    private int estanciasFinalizadasEliminadas;
    private LocalDateTime fechaReinicio;
}
