/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tics.tics.dtos;

import lombok.Data;

/**
 *
 * @author omar_arias12
 */
@Data
public class BusquedaRequest {
    private String termino; // Puede ser serie, marca, modelo o artículo
    

    public String getTermino() {
        return termino;
    }


    public void setTermino(String termino) {
        this.termino = termino;
    }
}
