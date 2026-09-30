package com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TarifaOficialStrategy implements TarifaStrategy {

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.OFICIAL;
    }

    @Override
    public BigDecimal calcularImporteSalida(long minutos) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean acumulaTiempoFinDeMes() {
        return false;
    }

    @Override
    public BigDecimal getTarifaPorMinuto() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
