package com.tics.tics.inventario.repository;

import com.tics.tics.inventario.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}