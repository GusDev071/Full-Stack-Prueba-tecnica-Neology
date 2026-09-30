package com.gustavo.prueba.tecnica.neology.pruebatecnica.controller;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.VehiculoDetalleResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.VehiculoRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.VehiculoResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.VehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/neo/vehiculos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Vehículos", description = "Endpoints para registro y consulta de vehículos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PostMapping("/oficiales")
    @Operation(summary = "Alta de vehículo oficial", description = "Registra un vehículo oficial que no paga tarifa de estacionamiento")
    public ResponseEntity<VehiculoResponse> registrarOficial(@Valid @RequestBody VehiculoRequest request) {
        VehiculoResponse response = vehiculoService.registrarOficial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/residentes")
    @Operation(summary = "Alta de vehículo residente", description = "Registra un vehículo residente con tarifa preferencial acumulada")
    public ResponseEntity<VehiculoResponse> registrarResidente(@Valid @RequestBody VehiculoRequest request) {
        VehiculoResponse response = vehiculoService.registrarResidente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/no-residentes")
    @Operation(summary = "Alta de vehículo no residente", description = "Registra un vehículo de visitante no residente con pago en ventanilla")
    public ResponseEntity<VehiculoResponse> registrarNoResidente(@Valid @RequestBody VehiculoRequest request) {
        VehiculoResponse response = vehiculoService.registrarNoResidente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar vehículos", description = "Obtiene el catálogo completo de vehículos registrados")
    public ResponseEntity<List<VehiculoResponse>> listarTodos() {
        return ResponseEntity.ok(vehiculoService.listarTodos());
    }

    @GetMapping("/{placa}")
    @Operation(summary = "Detalle de vehículo", description = "Obtiene información completa, estancias y acumulados de un vehículo por placa")
    public ResponseEntity<VehiculoDetalleResponse> obtenerDetalle(@PathVariable String placa) {
        return ResponseEntity.ok(vehiculoService.obtenerDetalle(placa));
    }
}
