/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tics.tics.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author omar_arias12
 */

@Entity
@Table(name = "equipos")
@Data
public class Equipo {
    @Id
    private String inventario; // Alfanumérico
    
    @ManyToOne
    @JoinColumn(name = "id_articulo", nullable = false)
    private Articulo articulo;
    
    @ManyToOne
    @JoinColumn(name = "id_modelo", nullable = false)
    private Modelo modelo;
    
    private String serie;
    private Integer cantidad = 1;

    private String caracteristicas;
    private String estado;
    
    @CreationTimestamp
    private LocalDateTime fechaRegistro;
}
