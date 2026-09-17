/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tics.tics.dtos;

/**
 *
 * @author omar_arias12
 */

import lombok.Data;

import lombok.Data;

@Data
public class EquipoMovilCreate {
    private String noInventario;
    private String noSerie;
    private String marca;
    private String modelo;
    private String ubicacion;
    private String observacion;
    private String estado;
    
    // Getters
    public String getNoInventario() { return noInventario; }
    public String getNoSerie() { return noSerie; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getUbicacion() { return ubicacion; }
    public String getObservacion() { return observacion; }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Setters
    public void setNoInventario(String noInventario) { this.noInventario = noInventario; }
    public void setNoSerie(String noSerie) { this.noSerie = noSerie; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}

