package com.tics.tics.solicitudes.repository;

import com.tics.tics.solicitudes.entity.FormularioDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormularioDetalleRepository extends JpaRepository<FormularioDetalle, Integer> {
    Optional<FormularioDetalle> findBySolicitud_IdSolicitud(Integer idSolicitud);
}