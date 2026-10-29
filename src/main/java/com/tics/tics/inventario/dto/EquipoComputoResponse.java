package com.tics.tics.inventario.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EquipoComputoResponse {
    private Integer idEquipoComputo;

    // Equipo base aplanado
    private String inventario;
    private String serie;
    private Integer cantidad;
    private String caracteristicas;
    private String estado;
    private String nombreArticulo;
    private String nombreMarca;
    private String nombreModelo;

    // Sala
    private Integer idSala;
    private String codigoSala;
    private String nombreSala;

    // Detalle técnico
    private String especificacionesHardware;
    private String sistemaOperativo;
    private String softwareInstalado;
    private String estadoTeclado;
    private String estadoMouse;
    private String estadoCableEthernet;
    private String estadoMonitor;
    private String procesador;
    private String ram;
    private String almacenamiento;

    // Contraseñas
    private String contrasena;
    private String passwordAdmin;   // NUEVO
    private String lastPassword;    // NUEVO

    // Estados
    private String estadoCpu;         // NUEVO (reemplaza estadoEquipo)
    private String estadoComputadora;

    private String observaciones;
    private Integer ultimoUsuarioActualizacion;

    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
}