package com.tics.tics.inventario.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EquipoVistaDTO {
    private String inventario;
    private String serie;
    private String nombreArticulo;
    private String marca;
    private String modelo;
    private Integer cantidad;
    private String caracteristicas;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private String estado;
}