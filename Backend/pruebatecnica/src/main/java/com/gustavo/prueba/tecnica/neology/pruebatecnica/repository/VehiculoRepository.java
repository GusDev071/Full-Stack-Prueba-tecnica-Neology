package com.gustavo.prueba.tecnica.neology.pruebatecnica.repository;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.TipoVehiculo;
import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {

    List<Vehiculo> findByTipo(TipoVehiculo tipo);

    boolean existsByPlaca(String placa);
}
