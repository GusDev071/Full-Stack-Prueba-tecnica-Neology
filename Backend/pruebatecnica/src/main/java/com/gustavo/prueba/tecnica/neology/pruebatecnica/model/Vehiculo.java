package com.gustavo.prueba.tecnica.neology.pruebatecnica.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehiculos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehiculo {

    @Id
    @Column(name = "placa", length = 20, nullable = false, unique = true)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 30, nullable = false)
    private TipoVehiculo tipo;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @PrePersist
    public void prePersist() {
        if (this.fechaAlta == null) {
            this.fechaAlta = LocalDateTime.now();
        }
        if (this.placa != null) {
            this.placa = this.placa.trim().toUpperCase();
        }
    }
}
