package com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TarifaNoResidenteStrategy implements TarifaStrategy {

    private static final BigDecimal TARIFA_POR_MINUTO = new BigDecimal("0.50");

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.NO_RESIDENTE;
    }

    @Override
    public BigDecimal calcularImporteSalida(long minutos) {
        return TARIFA_POR_MINUTO.multiply(BigDecimal.valueOf(minutos))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean acumulaTiempoFinDeMes() {
        return false;
    }

    @Override
    public BigDecimal getTarifaPorMinuto() {
        return TARIFA_POR_MINUTO.setScale(2, RoundingMode.HALF_UP);
    }
}
