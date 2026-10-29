package com.tics.tics.inventario.dto;

import lombok.Data;

@Data
public class ConteoInventarioDTO {
    /** Nombre del artículo o "Marca - Modelo" según agrupación. */
    private String categoria;

    private Long total;
    private Long alta;
    private Long baja;
    private Long mantenimiento;

    public ConteoInventarioDTO() {
        this.total = 0L;
        this.alta = 0L;
        this.baja = 0L;
        this.mantenimiento = 0L;
    }

    public ConteoInventarioDTO(String categoria) {
        this();
        this.categoria = categoria;
    }
}