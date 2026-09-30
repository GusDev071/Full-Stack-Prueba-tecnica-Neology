package com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TarifaStrategyTest {

    private TarifaOficialStrategy tarifaOficial;
    private TarifaResidenteStrategy tarifaResidente;
    private TarifaNoResidenteStrategy tarifaNoResidente;
    private TarifaContext tarifaContext;

    @BeforeEach
    void setUp() {
        tarifaOficial = new TarifaOficialStrategy();
        tarifaResidente = new TarifaResidenteStrategy();
        tarifaNoResidente = new TarifaNoResidenteStrategy();
        tarifaContext = new TarifaContext(List.of(tarifaOficial, tarifaResidente, tarifaNoResidente));
    }

    @Test
    @DisplayName("Vehículo Oficial: No paga importe al salir y no acumula tiempo")
    void testVehiculoOficialNoPaga() {
        BigDecimal importe = tarifaOficial.calcularImporteSalida(180);

        assertEquals(new BigDecimal("0.00"), importe);
        assertFalse(tarifaOficial.acumulaTiempoFinDeMes());
        assertEquals(new BigDecimal("0.00"), tarifaOficial.getTarifaPorMinuto());
    }

    @Test
    @DisplayName("Vehículo Residente: Salida $0.00, acumula tiempo y paga $0.05 por minuto a fin de mes")
    void testVehiculoResidenteCalculo() {
        BigDecimal importeSalida = tarifaResidente.calcularImporteSalida(100);
        assertEquals(new BigDecimal("0.00"), importeSalida);
        assertTrue(tarifaResidente.acumulaTiempoFinDeMes());

        // Para 100 minutos: 100 * 0.05 = 5.00
        BigDecimal importeFinDeMes = tarifaResidente.calcularImporteMensual(100);
        assertEquals(new BigDecimal("5.00"), importeFinDeMes);

        // Para 345 minutos: 345 * 0.05 = 17.25
        BigDecimal importeFinDeMes2 = tarifaResidente.calcularImporteMensual(345);
        assertEquals(new BigDecimal("17.25"), importeFinDeMes2);
    }

    @Test
    @DisplayName("Vehículo No Residente: Paga $0.50 por minuto al salir y no acumula")
    void testVehiculoNoResidenteCalculo() {
        assertFalse(tarifaNoResidente.acumulaTiempoFinDeMes());
        assertEquals(new BigDecimal("0.50"), tarifaNoResidente.getTarifaPorMinuto());

        // 60 minutos: 60 * 0.50 = 30.00
        BigDecimal importe = tarifaNoResidente.calcularImporteSalida(60);
        assertEquals(new BigDecimal("30.00"), importe);

        // 15 minutos: 15 * 0.50 = 7.50
        BigDecimal importe2 = tarifaNoResidente.calcularImporteSalida(15);
        assertEquals(new BigDecimal("7.50"), importe2);
    }

    @Test
    @DisplayName("TarifaContext resuelve la estrategia correcta según TipoVehiculo")
    void testTarifaContextResolucion() {
        assertEquals(TipoVehiculo.OFICIAL, tarifaContext.getStrategy(TipoVehiculo.OFICIAL).getTipo());
        assertEquals(TipoVehiculo.RESIDENTE, tarifaContext.getStrategy(TipoVehiculo.RESIDENTE).getTipo());
        assertEquals(TipoVehiculo.NO_RESIDENTE, tarifaContext.getStrategy(TipoVehiculo.NO_RESIDENTE).getTipo());
    }
}
