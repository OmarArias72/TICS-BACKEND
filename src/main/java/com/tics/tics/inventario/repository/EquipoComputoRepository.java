package com.tics.tics.inventario.repository;

import com.tics.tics.inventario.entity.EquipoComputo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoComputoRepository extends JpaRepository<EquipoComputo, Integer> {

    Optional<EquipoComputo> findByEquipo_Inventario(String inventario);

    List<EquipoComputo> findBySala_IdSala(Integer idSala);

    List<EquipoComputo> findBySala_CodigoSalaIgnoreCase(String codigoSala);

    List<EquipoComputo> findByEstadoComputadoraIgnoreCase(String estadoComputadora);

    List<EquipoComputo> findByEstadoEquipoIgnoreCase(String estadoEquipo);
}