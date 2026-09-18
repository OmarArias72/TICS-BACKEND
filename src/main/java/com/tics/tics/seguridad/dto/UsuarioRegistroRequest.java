package com.tics.tics.seguridad.dto;

import lombok.Data;

@Data
public class UsuarioRegistroRequest {
    private String nombre;
    private String apellidos;
    private String correo;
    private String password;
}