package com.tics.tics.solicitudes.dto;

import java.time.LocalDateTime;

public class HistorialSolicitudDTO {
    private Integer idHistorial;
    private String estadoAnterior;
    private String estadoNuevo;
    private String comentario;
    private LocalDateTime fechaCambio;
    private String usuarioAccionNombre;

    // Getters y Setters
    public Integer getIdHistorial() { return idHistorial; }
    public void setIdHistorial(Integer idHistorial) { this.idHistorial = idHistorial; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }
    public String getUsuarioAccionNombre() { return usuarioAccionNombre; }
    public void setUsuarioAccionNombre(String usuarioAccionNombre) { this.usuarioAccionNombre = usuarioAccionNombre; }
}