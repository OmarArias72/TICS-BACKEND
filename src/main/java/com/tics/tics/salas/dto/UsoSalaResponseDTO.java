package com.tics.tics.salas.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsoSalaResponseDTO {
    private Integer idUso;
    private Integer idSala;
    private String codigoSala;
    private String nombreSala;
    private String profesor;
    private String horaInicio;
    private String horaFin;
    private String proposito;
    private String estado;
    private LocalDateTime fechaRegistro;
}