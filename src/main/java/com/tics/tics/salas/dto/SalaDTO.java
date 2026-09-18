package com.tics.tics.salas.dto;

import lombok.Data;

@Data
public class SalaDTO {
    private Integer idSala;
    private String codigoSala;
    private String nombre;
    private String ubicacion;
    private String descripcion;
}