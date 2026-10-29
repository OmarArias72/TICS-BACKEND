package com.tics.tics.salas.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsoSalaResponseDTO {
    private Integer idUso;
    private Integer idSala;
    private String codigoSala;
    private String nombreSala;

    private Integer idUsuario;
    private String profesor;

    private String fecha;           // YYYY-MM-DD
    private String horaInicio;      // HH:mm
    private String horaFin;         // HH:mm
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private String proposito;
    private String estado;

    private String motivoCancelacion;
    private Integer idSolicitudRelacionada;
    private String adminModifico;   // nombre completo (opcional)

    private LocalDateTime fechaRegistro;
}