package com.tics.tics.salas.dto;

import lombok.Data;

@Data
public class ReasignarReservaRequest {
    private Integer idUso;
    private Integer idAdmin;
    private Integer idSalaDestino;
    private String fecha;
    private String horaInicio;
    private String horaFin;
}