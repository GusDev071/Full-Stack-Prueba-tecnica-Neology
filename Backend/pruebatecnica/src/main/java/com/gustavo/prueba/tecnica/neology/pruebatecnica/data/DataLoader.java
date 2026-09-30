package com.gustavo.prueba.tecnica.neology.pruebatecnica.data;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Estancia;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Residente;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Vehiculo;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.EstanciaRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.ResidenteRepository;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final VehiculoRepository vehiculoRepository;
    private final ResidenteRepository residenteRepository;
    private final EstanciaRepository estanciaRepository;

    @Override
    public void run(String... args) {
        if (vehiculoRepository.count() > 0) {
            return;
        }

        log.info("Inicializando datos de prueba en la base de datos H2...");

        // Vehículos oficiales
        Vehiculo oficial1 = Vehiculo.builder()
                .placa("OFI-001")
                .tipo(TipoVehiculo.OFICIAL)
                .fechaAlta(LocalDateTime.now().minusDays(10))
                .build();
        vehiculoRepository.save(oficial1);

        Vehiculo oficial2 = Vehiculo.builder()
                .placa("POL-911")
                .tipo(TipoVehiculo.OFICIAL)
                .fechaAlta(LocalDateTime.now().minusDays(5))
                .build();
        vehiculoRepository.save(oficial2);

        // Vehículos residentes
        Vehiculo residente1 = Vehiculo.builder()
                .placa("RES-101")
                .tipo(TipoVehiculo.RESIDENTE)
                .fechaAlta(LocalDateTime.now().minusDays(20))
                .build();
        vehiculoRepository.save(residente1);
        residenteRepository.save(Residente.builder().placa("RES-101").tiempoAcumulado(120L).build());

        Vehiculo residente2 = Vehiculo.builder()
                .placa("RES-202")
                .tipo(TipoVehiculo.RESIDENTE)
                .fechaAlta(LocalDateTime.now().minusDays(15))
                .build();
        vehiculoRepository.save(residente2);
        residenteRepository.save(Residente.builder().placa("RES-202").tiempoAcumulado(450L).build());

        // Vehículo no residente
        Vehiculo noResidente = Vehiculo.builder()
                .placa("VIS-505")
                .tipo(TipoVehiculo.NO_RESIDENTE)
                .fechaAlta(LocalDateTime.now().minusDays(2))
                .build();
        vehiculoRepository.save(noResidente);

        // Estancias históricas finalizadas
        estanciaRepository.save(Estancia.builder()
                .placa("OFI-001")
                .fechaHoraEntrada(LocalDateTime.now().minusHours(8))
                .fechaHoraSalida(LocalDateTime.now().minusHours(6))
                .minutos(120L)
                .importe(BigDecimal.ZERO)
                .build());

        estanciaRepository.save(Estancia.builder()
                .placa("VIS-505")
                .fechaHoraEntrada(LocalDateTime.now().minusHours(4))
                .fechaHoraSalida(LocalDateTime.now().minusHours(2))
                .minutos(120L)
                .importe(new BigDecimal("60.00"))
                .build());

        // Estancia actualmente activa para pruebas de salida
        estanciaRepository.save(Estancia.builder()
                .placa("RES-101")
                .fechaHoraEntrada(LocalDateTime.now().minusMinutes(45))
                .build());

        log.info("Datos iniciales cargados con éxito.");
    }
}
