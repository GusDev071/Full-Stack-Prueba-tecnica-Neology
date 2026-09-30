package com.gustavo.prueba.tecnica.neology.pruebatecnica.controller;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.InformePagosResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.PagoResidenteResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.ResidenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/neo/residentes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Residentes", description = "Endpoints para consulta e informe de pagos de vehículos residentes")
public class ResidenteController {

    private final ResidenteService residenteService;

    @GetMapping("/pagos")
    @Operation(summary = "Generar informe de pagos", description = "Genera el informe detallado de minutos acumulados e importes a cobrar a los residentes")
    public ResponseEntity<InformePagosResponse> generarInformePagos() {
        return ResponseEntity.ok(residenteService.generarInformePagos());
    }

    @GetMapping
    @Operation(summary = "Listar pagos de residentes", description = "Lista plana de residentes con sus minutos y montos calculados")
    public ResponseEntity<List<PagoResidenteResponse>> listarPagos() {
        return ResponseEntity.ok(residenteService.listarPagosResidentes());
    }
}
