package com.tics.tics.solicitudes.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SolicitudTecnicaResponseDTO {
    private Integer idSolicitud;
    private String nombreUsuario;
    private String correoUsuario; // Del usuario logueado
    private String tipoNombre;
    private String motivo;
    private String estado;
    private String motivoRechazo;

    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRevision;
    private String revisorNombre;

    // Datos si es que existen
    private FormularioDetalleDTO formulario;
    private TicketExternoDTO ticket;
    private List<HistorialSolicitudDTO> historial;

    // Getters y Setters
    public Integer getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }
    public String getTipoNombre() { return tipoNombre; }
    public void setTipoNombre(String tipoNombre) { this.tipoNombre = tipoNombre; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public LocalDateTime getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDateTime fechaRevision) { this.fechaRevision = fechaRevision; }
    public String getRevisorNombre() { return revisorNombre; }
    public void setRevisorNombre(String revisorNombre) { this.revisorNombre = revisorNombre; }
    public FormularioDetalleDTO getFormulario() { return formulario; }
    public void setFormulario(FormularioDetalleDTO formulario) { this.formulario = formulario; }
    public TicketExternoDTO getTicket() { return ticket; }
    public void setTicket(TicketExternoDTO ticket) { this.ticket = ticket; }
    public List<HistorialSolicitudDTO> getHistorial() { return historial; }
    public void setHistorial(List<HistorialSolicitudDTO> historial) { this.historial = historial; }
}