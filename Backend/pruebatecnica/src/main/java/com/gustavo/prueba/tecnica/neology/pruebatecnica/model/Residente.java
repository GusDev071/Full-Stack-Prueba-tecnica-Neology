package com.gustavo.prueba.tecnica.neology.pruebatecnica.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "residentes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Residente {

    @Id
    @Column(name = "placa", length = 20, nullable = false)
    private String placa;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placa", referencedColumnName = "placa", insertable = false, updatable = false)
    private Vehiculo vehiculo;

    @Column(name = "tiempo_acumulado", nullable = false)
    @Builder.Default
    private Long tiempoAcumulado = 0L;

    public void acumularMinutos(long minutos) {
        if (this.tiempoAcumulado == null) {
            this.tiempoAcumulado = 0L;
        }
        this.tiempoAcumulado += minutos;
    }

    public void reiniciarTiempo() {
        this.tiempoAcumulado = 0L;
    }
}
