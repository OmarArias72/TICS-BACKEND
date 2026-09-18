package com.tics.tics.seguridad.dto;

import lombok.Data;

@Data
public class UsuarioAdminCreateRequest {
    private String nombre;
    private String apellidos;
    private String correo;
    private String password;
    private Integer idRol;
    private Boolean activo;
}