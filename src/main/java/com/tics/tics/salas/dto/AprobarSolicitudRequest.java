package com.tics.tics.salas.dto;

import lombok.Data;

@Data
public class AprobarSolicitudRequest {
    private Integer idSolicitud;
    private Integer idAdmin;
    private Integer idSalaDestino;
    private String horaInicio;
    private String horaFin;
}