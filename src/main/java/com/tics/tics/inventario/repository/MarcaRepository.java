/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tics.tics.inventario.repository;

import java.util.Optional;
import com.tics.tics.inventario.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author omar_arias12
 */
public interface MarcaRepository extends JpaRepository<Marca, Integer> {
    Optional<Marca> findByNombreMarcaIgnoreCase(String nombre);
}
