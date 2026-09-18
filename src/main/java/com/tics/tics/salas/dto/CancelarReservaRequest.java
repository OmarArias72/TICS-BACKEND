package com.tics.tics.salas.dto;

import lombok.Data;

@Data
public class CancelarReservaRequest {
    private Integer idUso;
    private Integer idAdmin;
    private String motivoCancelacion;
}