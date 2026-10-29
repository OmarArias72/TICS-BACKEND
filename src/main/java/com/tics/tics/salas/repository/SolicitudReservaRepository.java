package com.tics.tics.salas.repository;

import com.tics.tics.salas.entity.SolicitudReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudReservaRepository extends JpaRepository<SolicitudReserva, Integer> {

    List<SolicitudReserva> findByEstadoOrderByFechaSolicitudDesc(String estado);

    // NUEVO — todas las solicitudes
    List<SolicitudReserva> findAllByOrderByFechaSolicitudDesc();

    // NUEVO — solicitudes de un profesor
    List<SolicitudReserva> findBySolicitante_IdUsuarioOrderByFechaSolicitudDesc(Integer idUsuario);
}