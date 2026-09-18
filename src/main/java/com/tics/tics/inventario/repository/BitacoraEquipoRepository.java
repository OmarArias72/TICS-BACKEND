package com.tics.tics.inventario.repository;

import com.tics.tics.inventario.entity.BitacoraEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BitacoraEquipoRepository extends JpaRepository<BitacoraEquipo, Integer> {
    List<BitacoraEquipo> findByEquipo_InventarioOrderByFechaHoraDesc(String inventario);
    List<BitacoraEquipo> findByUsuario_IdUsuarioOrderByFechaHoraDesc(Integer idUsuario);
}