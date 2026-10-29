package com.tics.tics.seguridad.dto;

import lombok.Data;

@Data
public class UsuarioUpdateRequest {
    private String nombre;
    private String apellidos;
    private String correo;
    /** Opcional: si viene null o vacío, no se cambia la contraseña. */
    private String password;
    private Integer idRol;
    private Boolean activo;
}