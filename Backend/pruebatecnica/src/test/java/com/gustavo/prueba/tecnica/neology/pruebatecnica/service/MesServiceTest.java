package com.gustavo.prueba.tecnica.neology.pruebatecnica.service;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.ResumenMesResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.EstanciaRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.ResidenteRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.impl.MesServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MesServiceTest {

    @Mock
    private ResidenteRepository residenteRepository;

    @Mock
    private EstanciaRepository estanciaRepository;

    @InjectMocks
    private MesServiceImpl mesService;

    @Test
    @DisplayName("Reiniciar mes resetea tiempos de residentes y elimina estancias cerradas")
    void testReiniciarMes() {
        when(residenteRepository.reiniciarTiemposAcumulados()).thenReturn(5);
        when(estanciaRepository.eliminarEstanciasFinalizadas()).thenReturn(24);

        ResumenMesResponse resumen = mesService.reiniciarMes();

        assertNotNull(resumen);
        assertEquals(5, resumen.getResidentesReiniciados());
        assertEquals(24, resumen.getEstanciasFinalizadasEliminadas());
        assertNotNull(resumen.getFechaReinicio());

        verify(residenteRepository).reiniciarTiemposAcumulados();
        verify(estanciaRepository).eliminarEstanciasFinalizadas();
    }
}
