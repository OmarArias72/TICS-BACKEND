/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tics.tics.modelos;

/**
 *
 * @author omar_arias12
 */


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "articulos")
@Data
public class Articulo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idArticulo;
    
    @Column(unique = true, nullable = false)
    private String nombreArticulo;
    
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}


