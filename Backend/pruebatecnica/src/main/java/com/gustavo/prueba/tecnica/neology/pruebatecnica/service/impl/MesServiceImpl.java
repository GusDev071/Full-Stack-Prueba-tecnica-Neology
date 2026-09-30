package com.gustavo.prueba.tecnica.neology.pruebatecnica.service.impl;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.ResumenMesResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.EstanciaRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.ResidenteRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.MesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MesServiceImpl implements MesService {

    private final ResidenteRepository residenteRepository;
    private final EstanciaRepository estanciaRepository;

    @Override
    @Transactional
    public ResumenMesResponse reiniciarMes() {
        int residentesActualizados = residenteRepository.reiniciarTiemposAcumulados();
        int estanciasEliminadas = estanciaRepository.eliminarEstanciasFinalizadas();

        return ResumenMesResponse.builder()
                .mensaje("Mes reiniciado correctamente: tiempos acumulados de residentes reseteados a 0 y estancias finalizadas eliminadas.")
                .residentesReiniciados(residentesActualizados)
                .estanciasFinalizadasEliminadas(estanciasEliminadas)
                .fechaReinicio(LocalDateTime.now())
                .build();
    }
}
