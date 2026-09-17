/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tics.tics.repositorios;

import java.util.Optional;
import com.tics.tics.modelos.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author omar_arias12
 */
public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {
    Optional<Articulo> findByNombreArticuloIgnoreCase(String nombre);
}
