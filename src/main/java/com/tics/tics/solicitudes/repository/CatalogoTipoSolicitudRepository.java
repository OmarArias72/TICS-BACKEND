package com.tics.tics.solicitudes.repository;

import com.tics.tics.solicitudes.entity.CatalogoTipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogoTipoSolicitudRepository extends JpaRepository<CatalogoTipoSolicitud, Integer> {

    Optional<CatalogoTipoSolicitud> findByNombreIgnoreCase(String nombre);

    List<CatalogoTipoSolicitud> findByActivoTrue();
}