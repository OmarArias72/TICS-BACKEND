package com.tics.tics.salas.dto;

import lombok.Data;

@Data
public class RechazarSolicitudRequest {
    private Integer idSolicitud;
    private Integer idAdmin;
    private String motivoRechazo;
}