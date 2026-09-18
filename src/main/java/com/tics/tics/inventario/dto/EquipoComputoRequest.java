package com.tics.tics.inventario.dto;

import lombok.Data;

@Data
public class EquipoComputoRequest {
    private String inventario;
    private Integer idSala;
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
    private String contrasena;
    private String estadoEquipo;
    private String estadoComputadora;
    private String observaciones;
    private Integer ultimoUsuarioActualizacion;
}