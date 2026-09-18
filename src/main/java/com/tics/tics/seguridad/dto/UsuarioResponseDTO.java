package com.tics.tics.seguridad.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String apellidos;
    private String correo;
    private String nombreRol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}