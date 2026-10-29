package com.tics.tics.solicitudes.repository;

import com.tics.tics.solicitudes.entity.TicketExterno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketExternoRepository extends JpaRepository<TicketExterno, Integer> {
    Optional<TicketExterno> findBySolicitud_IdSolicitud(Integer idSolicitud);
}