package com.gustavo.prueba.tecnica.neology.pruebatecnica.repository;

import com.gustavo.prueba.tecnica.neology.pruebatecnica.model.Residente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidenteRepository extends JpaRepository<Residente, String> {

    @Modifying
    @Query("UPDATE Residente r SET r.tiempoAcumulado = 0")
    int reiniciarTiemposAcumulados();
}
