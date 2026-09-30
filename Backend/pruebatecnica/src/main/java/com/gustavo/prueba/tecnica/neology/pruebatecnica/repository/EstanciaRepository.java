package com.gustavo.prueba.tecnica.neology.pruebatecnica.repository;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Estancia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstanciaRepository extends JpaRepository<Estancia, Long> {

    Optional<Estancia> findByPlacaAndFechaHoraSalidaIsNull(String placa);

    List<Estancia> findByPlacaOrderByFechaHoraEntradaDesc(String placa);

    List<Estancia> findByFechaHoraSalidaIsNull();

    List<Estancia> findAllByOrderByFechaHoraEntradaDesc();

    @Modifying
    @Query("DELETE FROM Estancia e WHERE e.fechaHoraSalida IS NOT NULL")
    int eliminarEstanciasFinalizadas();
}
