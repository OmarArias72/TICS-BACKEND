package com.tics.tics.solicitudes.dto;

public class CrearSolicitudRequest {
    private Integer idTipo; // 1=Password, 2=Auth, 3=Otro
    private String motivo;

    public Integer getIdTipo() { return idTipo; }
    public void setIdTipo(Integer idTipo) { this.idTipo = idTipo; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}