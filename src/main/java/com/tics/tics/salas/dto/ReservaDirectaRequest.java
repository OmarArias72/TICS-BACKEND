package com.tics.tics.salas.dto;

import lombok.Data;

@Data
public class ReservaDirectaRequest {
    private Integer idSala;
    private Integer idUsuario;
    private String fecha; // Formato YYYY-MM-DD
    private String horaInicio; // Formato HH:mm
    private String horaFin; // Formato HH:mm
    private String proposito;
}