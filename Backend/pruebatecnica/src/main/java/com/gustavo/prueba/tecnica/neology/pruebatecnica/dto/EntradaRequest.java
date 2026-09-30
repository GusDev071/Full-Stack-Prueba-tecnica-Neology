package com.gustavo.prueba.tecnica.neology.pruebatecnica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntradaRequest {

    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "La placa solo debe contener letras, números y guiones")
    private String placa;

    /**
     * Opcional: Permite enviar una fecha/hora personalizada (útil para pruebas o integraciones)
     */
    private LocalDateTime fechaHora;
}
