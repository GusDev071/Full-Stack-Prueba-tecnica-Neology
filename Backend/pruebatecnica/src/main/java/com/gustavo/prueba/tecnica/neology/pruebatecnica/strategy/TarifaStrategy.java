package com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;

import java.math.BigDecimal;

public interface TarifaStrategy {

    TipoVehiculo getTipo();

    BigDecimal calcularImporteSalida(long minutos);

    boolean acumulaTiempoFinDeMes();

    BigDecimal getTarifaPorMinuto();

    default BigDecimal calcularImporteMensual(long minutosAcumulados) {
        return getTarifaPorMinuto().multiply(BigDecimal.valueOf(minutosAcumulados));
    }
}
