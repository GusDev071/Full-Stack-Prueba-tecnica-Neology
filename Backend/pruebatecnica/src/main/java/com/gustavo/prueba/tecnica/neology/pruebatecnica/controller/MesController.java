package com.gustavo.prueba.tecnica.neology.pruebatecnica.controller;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.ResumenMesResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.MesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo/mes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Mes / Periodo", description = "Endpoints de cierre y reinicio de ciclo mensual")
public class MesController {

    private final MesService mesService;

    @PostMapping("/iniciar")
    @Operation(summary = "Reiniciar mes", description = "Resetea a 0 los tiempos acumulados de los residentes y elimina las estancias finalizadas del periodo")
    public ResponseEntity<ResumenMesResponse> reiniciarMes() {
        return ResponseEntity.ok(mesService.reiniciarMes());
    }
}
