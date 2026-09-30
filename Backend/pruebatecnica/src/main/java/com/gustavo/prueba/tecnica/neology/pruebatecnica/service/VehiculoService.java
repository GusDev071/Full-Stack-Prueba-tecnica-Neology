package com.gustavo.prueba.tecnica.neology.pruebatecnica.service;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.VehiculoDetalleResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.VehiculoRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.VehiculoResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Vehiculo;

import java.util.List;

public interface VehiculoService {

    VehiculoResponse registrarOficial(VehiculoRequest request);

    VehiculoResponse registrarResidente(VehiculoRequest request);

    VehiculoResponse registrarNoResidente(VehiculoRequest request);

    List<VehiculoResponse> listarTodos();

    VehiculoDetalleResponse obtenerDetalle(String placa);

    Vehiculo obtenerOcrearVehiculo(String placa);
}
