package com.gustavo.prueba.tecnica.neology.pruebatecnica.service;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EntradaRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EntradaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EstanciaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.SalidaRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.SalidaResponse;

import java.util.List;

public interface EstanciaService {

    EntradaResponse registrarEntrada(EntradaRequest request);

    SalidaResponse registrarSalida(SalidaRequest request);

    List<EstanciaResponse> listarEstancias();

    List<EstanciaResponse> listarEstanciasActivas();
}
