package com.tics.tics.salas.dto;

import lombok.Data;

@Data
public class SolicitudReservaResponseDTO {
    private Integer idSolicitud;
    private Integer idSala;
    private String codigoSala;
    private String nombreSala;
    private String profesor;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String justificacion;
    private String estado;
}