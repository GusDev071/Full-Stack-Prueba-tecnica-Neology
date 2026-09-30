package com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TarifaResidenteStrategy implements TarifaStrategy {

    private static final BigDecimal TARIFA_POR_MINUTO = new BigDecimal("0.05");

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.RESIDENTE;
    }

    @Override
    public BigDecimal calcularImporteSalida(long minutos) {
        // Los residentes acumulan tiempo para el pago de fin de mes
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean acumulaTiempoFinDeMes() {
        return true;
    }

    @Override
    public BigDecimal getTarifaPorMinuto() {
        return TARIFA_POR_MINUTO.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularImporteMensual(long minutosAcumulados) {
        return TARIFA_POR_MINUTO.multiply(BigDecimal.valueOf(minutosAcumulados))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
