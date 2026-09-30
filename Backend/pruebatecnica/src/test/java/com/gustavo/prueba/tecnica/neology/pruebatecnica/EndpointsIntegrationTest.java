package com.gustavo.prueba.tecnica.neology.pruebatecnica;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EntradaRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.SalidaRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.VehiculoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST neo/vehiculos/oficiales registra vehículo oficial correctamente")
    void testRegistroOficial() throws Exception {
        VehiculoRequest request = VehiculoRequest.builder().placa("OFI-999").build();

        mockMvc.perform(post("/neo/vehiculos/oficiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa", is("OFI-999")))
                .andExpect(jsonPath("$.tipo", is("OFICIAL")));
    }

    @Test
    @DisplayName("POST neo/vehiculos/residentes registra residente y permite consulta")
    void testRegistroResidente() throws Exception {
        VehiculoRequest request = VehiculoRequest.builder().placa("RES-888").build();

        mockMvc.perform(post("/neo/vehiculos/residentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa", is("RES-888")))
                .andExpect(jsonPath("$.tipo", is("RESIDENTE")))
                .andExpect(jsonPath("$.tiempoAcumulado", is(0)));
    }

    @Test
    @DisplayName("Flujo completo: Entrada y Salida de vehículo no residente con cobro a $0.50/min")
    void testFlujoNoResidenteEntradaSalida() throws Exception {
        String placa = "TUR-123";
        LocalDateTime base = LocalDateTime.now().withNano(0);
        LocalDateTime entrada = base.minusMinutes(30);
        LocalDateTime salida = base;

        // 1. Entrada
        EntradaRequest reqEntrada = EntradaRequest.builder().placa(placa).fechaHora(entrada).build();
        mockMvc.perform(post("/neo/estancias/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa", is(placa)))
                .andExpect(jsonPath("$.tipoVehiculo", is("NO_RESIDENTE")));

        // 2. Salida
        SalidaRequest reqSalida = SalidaRequest.builder().placa(placa).fechaHora(salida).build();
        mockMvc.perform(post("/neo/estancias/salida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqSalida)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa", is(placa)))
                .andExpect(jsonPath("$.minutos", is(30)))
                .andExpect(jsonPath("$.importeCobrado", is(15.00))); // 30 * 0.50 = 15.00
    }

    @Test
    @DisplayName("GET neo/residentes/pagos retorna informe con residentes y totales")
    void testInformePagosResidentes() throws Exception {
        mockMvc.perform(get("/neo/residentes/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResidentes", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.residentes", notNullValue()));
    }

    @Test
    @DisplayName("POST neo/mes/iniciar reinicia mes y tiempos acumulados")
    void testReiniciarMesEndpoint() throws Exception {
        mockMvc.perform(post("/neo/mes/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", containsString("Mes reiniciado correctamente")));
    }
}
