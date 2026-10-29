package com.tics.tics.inventario.dto;

import lombok.Data;
import java.util.List;

@Data
public class ActualizarEstadoMasivoRequest {
    private List<String> inventarios; // Lista de números de inventario a actualizar
    private String nuevoEstado;       // "MANTENIMIENTO", "BAJA", "ALTA", etc.
}