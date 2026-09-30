package com.gustavo.prueba.tecnica.neology.pruebatecnica.service;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.InformePagosResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Residente;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.ResidenteRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.impl.ResidenteServiceImpl;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaContext;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaNoResidenteStrategy;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaOficialStrategy;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaResidenteStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResidenteServiceTest {

    @Mock
    private ResidenteRepository residenteRepository;

    @Spy
    private TarifaContext tarifaContext = new TarifaContext(List.of(
            new TarifaOficialStrategy(),
            new TarifaResidenteStrategy(),
            new TarifaNoResidenteStrategy()
    ));

    @InjectMocks
    private ResidenteServiceImpl residenteService;

    @Test
    @DisplayName("Generar informe de pagos calcula correctamente acumulados y montos a pagar")
    void testGenerarInformePagos() {
        Residente r1 = Residente.builder().placa("RES-001").tiempoAcumulado(100L).build(); // 100 * 0.05 = 5.00
        Residente r2 = Residente.builder().placa("RES-002").tiempoAcumulado(200L).build(); // 200 * 0.05 = 10.00

        when(residenteRepository.findAll()).thenReturn(List.of(r1, r2));

        InformePagosResponse informe = residenteService.generarInformePagos();

        assertNotNull(informe);
        assertEquals(2, informe.getTotalResidentes());
        assertEquals(300L, informe.getTotalMinutosAcumulados());
        assertEquals(new BigDecimal("15.00"), informe.getTotalCobro());

        assertEquals("RES-001", informe.getResidentes().get(0).getPlaca());
        assertEquals(new BigDecimal("5.00"), informe.getResidentes().get(0).getTotalPagar());

        assertEquals("RES-002", informe.getResidentes().get(1).getPlaca());
        assertEquals(new BigDecimal("10.00"), informe.getResidentes().get(1).getTotalPagar());
    }
}
