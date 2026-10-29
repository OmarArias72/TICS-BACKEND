package com.tics.tics.inventario.repository;

import com.tics.tics.inventario.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, String> {

    List<Equipo> findBySerieContainingIgnoreCase(String serie);

    List<Equipo> findDistinctByModelo_Marca_NombreMarcaContainingIgnoreCase(String marca);

    List<Equipo> findByModelo_NombreModeloContainingIgnoreCase(String modelo);

    List<Equipo> findByArticulo_NombreArticuloContainingIgnoreCase(String articulo);

    @Query("SELECT e FROM Equipo e WHERE " +
            "LOWER(e.inventario) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(e.serie) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(e.articulo.nombreArticulo) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(e.modelo.nombreModelo) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(e.modelo.marca.nombreMarca) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Equipo> searchByGlobalTerm(@Param("term") String term);

    // 1. Búsqueda exclusiva por rango de fechas
    @Query("SELECT e FROM Equipo e WHERE e.fechaRegistro >= :fechaInicio AND e.fechaRegistro <= :fechaFin")
    List<Equipo> buscarPorRangoFechas(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // 2. Búsqueda por rango de fechas Y tipo de artículo
    @Query("SELECT e FROM Equipo e WHERE e.fechaRegistro >= :fechaInicio AND e.fechaRegistro <= :fechaFin " +
            "AND LOWER(e.articulo.nombreArticulo) LIKE LOWER(CONCAT('%', :articulo, '%'))")
    List<Equipo> buscarPorRangoFechasYArticulo(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("articulo") String articulo
    );

    // ==================== CONTEOS POR AGRUPACIÓN ====================

    /** [nombreArticulo, estado, count] */
    @Query("SELECT e.articulo.nombreArticulo, e.estado, COUNT(e) " +
            "FROM Equipo e " +
            "GROUP BY e.articulo.nombreArticulo, e.estado " +
            "ORDER BY e.articulo.nombreArticulo ASC")
    List<Object[]> contarPorArticuloYEstado();

    /** [nombreModelo, nombreMarca, estado, count] */
    @Query("SELECT e.modelo.nombreModelo, e.modelo.marca.nombreMarca, e.estado, COUNT(e) " +
            "FROM Equipo e " +
            "GROUP BY e.modelo.nombreModelo, e.modelo.marca.nombreMarca, e.estado " +
            "ORDER BY e.modelo.marca.nombreMarca ASC, e.modelo.nombreModelo ASC")
    List<Object[]> contarPorModeloYEstado();
}
