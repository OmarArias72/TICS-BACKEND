package com.tics.tics.solicitudes.repository;

import com.tics.tics.solicitudes.entity.EstadoSolicitud;
import com.tics.tics.solicitudes.entity.SolicitudTecnica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudTecnicaRepository extends JpaRepository<SolicitudTecnica, Integer> {

    // 1. Para el Alumno: ver su historial
    List<SolicitudTecnica> findByUsuario_IdUsuarioOrderByFechaSolicitudDesc(Integer idUsuario);

    // 2. Anti-spam: contar solicitudes activas del alumno
    @Query("SELECT COUNT(s) FROM SolicitudTecnica s WHERE s.usuario.idUsuario = :idUsuario " +
            "AND s.estado IN ('PENDIENTE', 'APROBADA', 'FORMULARIO_ENVIADO', 'ESPERA_TICKET', 'TICKET_CREADO')")
    long countSolicitudesActivasByUsuario(@Param("idUsuario") Integer idUsuario);

    // 3. Anti-spam (Cooldown): buscar la última solicitud del mismo tipo
    Optional<SolicitudTecnica> findFirstByUsuario_IdUsuarioAndTipo_IdTipoOrderByFechaSolicitudDesc(
            Integer idUsuario, Integer idTipo);

    // 4. Para el Admin/Operador: Panel con filtros
    // Filtro "Pendientes"
    List<SolicitudTecnica> findByEstadoOrderByFechaSolicitudAsc(EstadoSolicitud estado);

    // Filtro "Sin ID (Aprobadas o Formulario Enviado listas para ticket)"
    List<SolicitudTecnica> findByEstadoInOrderByFechaSolicitudAsc(List<EstadoSolicitud> estados);

    // Filtro "Todas"
    List<SolicitudTecnica> findAllByOrderByFechaSolicitudDesc();

    // 5. Limpieza de bloqueos (opcional, para un cron job)
    @Query("SELECT s FROM SolicitudTecnica s WHERE s.estado = 'ESPERA_TICKET' AND s.fechaBloqueo < :fechaLimite")
    List<SolicitudTecnica> findBloqueosExpirados(@Param("fechaLimite") LocalDateTime fechaLimite);
}