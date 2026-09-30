package com.gustavo.prueba.tecnica.neology.pruebatecnica.controller;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EntradaRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EntradaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EstanciaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.SalidaRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.SalidaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.EstanciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/neo/estancias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Estancias", description = "Endpoints para registro de entrada y salida de vehículos")
public class EstanciaController {

    private final EstanciaService estanciaService;

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada", description = "Registra el ingreso de un vehículo al estacionamiento")
    public ResponseEntity<EntradaResponse> registrarEntrada(@Valid @RequestBody EntradaRequest request) {
        EntradaResponse response = estanciaService.registrarEntrada(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/salida")
    @Operation(summary = "Registrar salida", description = "Registra la salida del vehículo, calculando importe o acumulando minutos según tipo")
    public ResponseEntity<SalidaResponse> registrarSalida(@Valid @RequestBody SalidaRequest request) {
        SalidaResponse response = estanciaService.registrarSalida(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar estancias", description = "Consulta el historial general de estancias registradas")
    public ResponseEntity<List<EstanciaResponse>> listarEstancias() {
        return ResponseEntity.ok(estanciaService.listarEstancias());
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar estancias activas", description = "Consulta los vehículos que actualmente se encuentran en el estacionamiento")
    public ResponseEntity<List<EstanciaResponse>> listarEstanciasActivas() {
        return ResponseEntity.ok(estanciaService.listarEstanciasActivas());
    }
}
