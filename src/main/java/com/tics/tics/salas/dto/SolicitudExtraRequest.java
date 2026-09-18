package com.tics.tics.salas.dto;

import lombok.Data;

@Data
public class SolicitudExtraRequest {
    private Integer idSala;
    private Integer idUsuario;
    private String fecha; // Formato YYYY-MM-DD
    private String horaInicio; // Formato HH:mm
    private String horaFin; // Formato HH:mm
    private String justificacion;
}