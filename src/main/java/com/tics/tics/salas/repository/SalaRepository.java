package com.tics.tics.salas.repository;

import com.tics.tics.salas.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Integer> {

    Optional<Sala> findByCodigoSalaIgnoreCase(String codigoSala);

    List<Sala> findByNombreContainingIgnoreCaseOrUbicacionContainingIgnoreCase(String nombre, String ubicacion);

    boolean existsByCodigoSalaIgnoreCase(String codigoSala);
}