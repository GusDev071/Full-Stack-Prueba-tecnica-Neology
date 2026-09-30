package com.gustavo.prueba.tecnica.neology.pruebatecnica.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estancias", indexes = {
    @Index(name = "idx_estancia_placa", columnList = "placa"),
    @Index(name = "idx_estancia_salida", columnList = "fecha_hora_salida")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estancia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "placa", length = 20, nullable = false)
    private String placa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "placa", referencedColumnName = "placa", insertable = false, updatable = false)
    private Vehiculo vehiculo;

    @Column(name = "fecha_hora_entrada", nullable = false)
    private LocalDateTime fechaHoraEntrada;

    @Column(name = "fecha_hora_salida")
    private LocalDateTime fechaHoraSalida;

    @Column(name = "minutos")
    private Long minutos;

    @Column(name = "importe", precision = 10, scale = 2)
    private BigDecimal importe;

    public boolean estaActiva() {
        return this.fechaHoraSalida == null;
    }
}
