package com.gustavo.prueba.tecnica.neology.pruebatecnica.service;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.InformePagosResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.PagoResidenteResponse;

import java.util.List;

public interface ResidenteService {

    InformePagosResponse generarInformePagos();

    List<PagoResidenteResponse> listarPagosResidentes();
}
