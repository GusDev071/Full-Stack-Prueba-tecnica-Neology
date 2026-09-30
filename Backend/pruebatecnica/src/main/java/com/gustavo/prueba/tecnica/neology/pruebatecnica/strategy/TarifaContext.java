package com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class TarifaContext {

    private final Map<TipoVehiculo, TarifaStrategy> strategies = new EnumMap<>(TipoVehiculo.class);

    public TarifaContext(List<TarifaStrategy> strategyList) {
        for (TarifaStrategy strategy : strategyList) {
            strategies.put(strategy.getTipo(), strategy);
        }
    }

    public TarifaStrategy getStrategy(TipoVehiculo tipo) {
        TarifaStrategy strategy = strategies.get(tipo);
        if (strategy == null) {
            throw new IllegalArgumentException("No existe estrategia de tarifa registrada para el tipo: " + tipo);
        }
        return strategy;
    }
}
