/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tics.tics.repositorios;

import java.util.List;
import com.tics.tics.modelos.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author omar_arias12
 */


public interface EquipoRepository extends JpaRepository<Equipo, String> {
    List<Equipo> findBySerieContainingIgnoreCase(String serie);
    List<Equipo> findDistinctByModelo_Marca_NombreMarcaContainingIgnoreCase(String marca);
    List<Equipo> findByModelo_NombreModeloContainingIgnoreCase(String modelo);
    List<Equipo> findByArticulo_NombreArticuloContainingIgnoreCase(String articulo);
}
