package com.tics.tics.salas.repository;

import com.tics.tics.salas.entity.UsoSala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsoSalaRepository extends JpaRepository<UsoSala, Integer> {

    List<UsoSala> findBySala_IdSalaAndFechaInicioBetweenOrderByFechaInicioAsc(
            Integer idSala, LocalDateTime inicioDia, LocalDateTime finDia
    );

    // NUEVO — todas las reservas de un día (para admin)
    List<UsoSala> findByFechaInicioBetweenOrderByFechaInicioAsc(
            LocalDateTime inicioDia, LocalDateTime finDia
    );

    // NUEVO — historial de reservas de un usuario
    List<UsoSala> findByUsuario_IdUsuarioOrderByFechaInicioDesc(Integer idUsuario);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UsoSala u " +
            "WHERE u.sala.idSala = :idSala " +
            "AND u.estado IN ('Reservada', 'Confirmada') " +
            "AND (:fechaInicio < u.fechaFin AND :fechaFin > u.fechaInicio)")
    boolean existeEmpalmeHorario(
            @Param("idSala") Integer idSala,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UsoSala u " +
            "WHERE u.sala.idSala = :idSala " +
            "AND u.idUso <> :idUsoExcluir " +
            "AND u.estado IN ('Reservada', 'Confirmada') " +
            "AND (:fechaInicio < u.fechaFin AND :fechaFin > u.fechaInicio)")
    boolean existeEmpalmeHorarioExcluyendoActual(
            @Param("idSala") Integer idSala,
            @Param("idUsoExcluir") Integer idUsoExcluir,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}