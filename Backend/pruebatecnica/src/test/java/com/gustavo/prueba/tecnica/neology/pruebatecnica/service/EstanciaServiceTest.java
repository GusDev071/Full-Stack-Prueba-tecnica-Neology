package com.gustavo.prueba.tecnica.neology.pruebatecnica.service;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EntradaRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.EntradaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.SalidaRequest;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.dto.SalidaResponse;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.exception.ReglaNegocioException;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Estancia;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Residente;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Vehiculo;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.EstanciaRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.ResidenteRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.VehiculoRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.service.impl.EstanciaServiceImpl;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaContext;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaNoResidenteStrategy;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaOficialStrategy;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.strategy.TarifaResidenteStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstanciaServiceTest {

    @Mock
    private EstanciaRepository estanciaRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private ResidenteRepository residenteRepository;

    @Mock
    private VehiculoService vehiculoService;

    @Spy
    private TarifaContext tarifaContext = new TarifaContext(List.of(
            new TarifaOficialStrategy(),
            new TarifaResidenteStrategy(),
            new TarifaNoResidenteStrategy()
    ));

    @InjectMocks
    private EstanciaServiceImpl estanciaService;

    @Test
    @DisplayName("Registrar entrada exitosa")
    void testRegistrarEntradaExitoso() {
        String placa = "ABC-123";
        Vehiculo vehiculo = Vehiculo.builder()
                .placa(placa)
                .tipo(TipoVehiculo.NO_RESIDENTE)
                .build();

        when(vehiculoService.obtenerOcrearVehiculo(placa)).thenReturn(vehiculo);
        when(estanciaRepository.findByPlacaAndFechaHoraSalidaIsNull(placa)).thenReturn(Optional.empty());
        when(estanciaRepository.save(any(Estancia.class))).thenAnswer(invocation -> {
            Estancia e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        EntradaRequest request = EntradaRequest.builder().placa(placa).build();
        EntradaResponse response = estanciaService.registrarEntrada(request);

        assertNotNull(response);
        assertEquals(placa, response.getPlaca());
        assertEquals(TipoVehiculo.NO_RESIDENTE, response.getTipoVehiculo());
        assertNotNull(response.getFechaHoraEntrada());
        verify(estanciaRepository).save(any(Estancia.class));
    }

    @Test
    @DisplayName("Registrar entrada cuando el vehículo ya tiene estancia activa lanza excepción")
    void testRegistrarEntradaYaActivoLanzaExcepcion() {
        String placa = "ABC-123";
        Vehiculo vehiculo = Vehiculo.builder().placa(placa).tipo(TipoVehiculo.RESIDENTE).build();

        when(vehiculoService.obtenerOcrearVehiculo(placa)).thenReturn(vehiculo);
        when(estanciaRepository.findByPlacaAndFechaHoraSalidaIsNull(placa)).thenReturn(Optional.of(
                Estancia.builder().id(1L).placa(placa).fechaHoraEntrada(LocalDateTime.now().minusHours(1)).build()
        ));

        EntradaRequest request = EntradaRequest.builder().placa(placa).build();

        assertThrows(ReglaNegocioException.class, () -> estanciaService.registrarEntrada(request));
        verify(estanciaRepository, never()).save(any(Estancia.class));
    }

    @Test
    @DisplayName("Registrar salida de vehículo no residente cobra $0.50 por minuto")
    void testRegistrarSalidaNoResidente() {
        String placa = "VIS-999";
        LocalDateTime entrada = LocalDateTime.now().minusMinutes(60);
        LocalDateTime salida = LocalDateTime.now();

        Estancia estanciaActiva = Estancia.builder()
                .id(10L)
                .placa(placa)
                .fechaHoraEntrada(entrada)
                .build();

        Vehiculo vehiculo = Vehiculo.builder()
                .placa(placa)
                .tipo(TipoVehiculo.NO_RESIDENTE)
                .build();

        when(estanciaRepository.findByPlacaAndFechaHoraSalidaIsNull(placa)).thenReturn(Optional.of(estanciaActiva));
        when(vehiculoRepository.findById(placa)).thenReturn(Optional.of(vehiculo));
        when(estanciaRepository.save(any(Estancia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SalidaRequest request = SalidaRequest.builder()
                .placa(placa)
                .fechaHora(salida)
                .build();

        SalidaResponse response = estanciaService.registrarSalida(request);

        assertNotNull(response);
        assertEquals(placa, response.getPlaca());
        assertEquals(TipoVehiculo.NO_RESIDENTE, response.getTipoVehiculo());
        assertEquals(new BigDecimal("30.00"), response.getImporteCobrado());
        assertNull(response.getTiempoAcumuladoTotal());
    }

    @Test
    @DisplayName("Registrar salida de vehículo oficial no genera cobro ($0.00)")
    void testRegistrarSalidaOficial() {
        String placa = "OFI-100";
        LocalDateTime entrada = LocalDateTime.now().minusMinutes(120);
        LocalDateTime salida = LocalDateTime.now();

        Estancia estanciaActiva = Estancia.builder()
                .id(11L)
                .placa(placa)
                .fechaHoraEntrada(entrada)
                .build();

        Vehiculo vehiculo = Vehiculo.builder()
                .placa(placa)
                .tipo(TipoVehiculo.OFICIAL)
                .build();

        when(estanciaRepository.findByPlacaAndFechaHoraSalidaIsNull(placa)).thenReturn(Optional.of(estanciaActiva));
        when(vehiculoRepository.findById(placa)).thenReturn(Optional.of(vehiculo));
        when(estanciaRepository.save(any(Estancia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SalidaRequest request = SalidaRequest.builder()
                .placa(placa)
                .fechaHora(salida)
                .build();

        SalidaResponse response = estanciaService.registrarSalida(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("0.00"), response.getImporteCobrado());
        assertEquals(TipoVehiculo.OFICIAL, response.getTipoVehiculo());
    }

    @Test
    @DisplayName("Registrar salida de residente acumula minutos para fin de mes")
    void testRegistrarSalidaResidente() {
        String placa = "RES-555";
        LocalDateTime entrada = LocalDateTime.now().minusMinutes(40);
        LocalDateTime salida = LocalDateTime.now();

        Estancia estanciaActiva = Estancia.builder()
                .id(12L)
                .placa(placa)
                .fechaHoraEntrada(entrada)
                .build();

        Vehiculo vehiculo = Vehiculo.builder()
                .placa(placa)
                .tipo(TipoVehiculo.RESIDENTE)
                .build();

        Residente residente = Residente.builder()
                .placa(placa)
                .tiempoAcumulado(100L)
                .build();

        when(estanciaRepository.findByPlacaAndFechaHoraSalidaIsNull(placa)).thenReturn(Optional.of(estanciaActiva));
        when(vehiculoRepository.findById(placa)).thenReturn(Optional.of(vehiculo));
        when(residenteRepository.findById(placa)).thenReturn(Optional.of(residente));
        when(estanciaRepository.save(any(Estancia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SalidaRequest request = SalidaRequest.builder()
                .placa(placa)
                .fechaHora(salida)
                .build();

        SalidaResponse response = estanciaService.registrarSalida(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("0.00"), response.getImporteCobrado());
        assertEquals(140L, response.getTiempoAcumuladoTotal());
        verify(residenteRepository).save(residente);
    }

    @Test
    @DisplayName("Registrar salida sin estancia activa lanza excepción")
    void testRegistrarSalidaSinEstanciaActiva() {
        String placa = "DESCONOCIDO";
        when(estanciaRepository.findByPlacaAndFechaHoraSalidaIsNull(placa)).thenReturn(Optional.empty());

        SalidaRequest request = SalidaRequest.builder().placa(placa).build();

        assertThrows(ReglaNegocioException.class, () -> estanciaService.registrarSalida(request));
    }
}
