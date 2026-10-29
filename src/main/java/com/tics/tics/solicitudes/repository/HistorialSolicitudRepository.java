package com.tics.tics.solicitudes.repository;

import com.tics.tics.solicitudes.entity.HistorialSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud, Integer> {
    List<HistorialSolicitud> findBySolicitud_IdSolicitudOrderByFechaCambioDesc(Integer idSolicitud);
}