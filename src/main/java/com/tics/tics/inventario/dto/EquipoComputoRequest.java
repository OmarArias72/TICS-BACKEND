package com.tics.tics.inventario.dto;

import lombok.Data;

@Data
public class EquipoComputoRequest {

    private Integer idEquipoComputo;

    // ==================== Equipo base ====================
    private String inventario;
    private String nombreArticulo;
    private String nombreMarca;
    private String nombreModelo;
    private String serie;
    private Integer cantidad;
    private String caracteristicas;
    private String estado;           // ALTA | MANTENIMIENTO | BAJA

    // ==================== Relación con Sala ====================
    private Integer idSala;

    // ==================== Detalle técnico ====================
    private String especificacionesHardware;
    private String sistemaOperativo;
    private String softwareInstalado;

    // Periféricos (valores validados por CHECKs en BD)
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
    private String estadoCpu;         // 'Funciona', 'No funciona', 'Con fallas', 'En revisión', 'Requiere mantenimiento'
    private String estadoComputadora; // 'Funcionando', 'No funciona', 'Requiere revisión', 'Requiere Mantenimiento'

    private String observaciones;
    private Integer ultimoUsuarioActualizacion;
}