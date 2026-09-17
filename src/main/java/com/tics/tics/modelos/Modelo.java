/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tics.tics.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 *
 * @author omar_arias12
 */
@Entity
@Table(name = "modelos")
@Data
public class Modelo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idModelo;
    
    @ManyToOne
    @JoinColumn(name = "id_marca", nullable = false)
    private Marca marca;
    
    @Column(nullable = false)
    private String nombreModelo;
}

