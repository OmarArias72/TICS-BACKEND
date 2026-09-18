package com.tics.tics.inventario.dto;

import lombok.Data;

@Data
public class EquipoMovilCreate {
    private String inventario;
    private String nombreArticulo;
    private String nombreMarca;
    private String nombreModelo;
    private String serie;
    private Integer cantidad;
    private String caracteristicas;
    private String estado;
    private Integer idUsuario;
    private String observacion;
}