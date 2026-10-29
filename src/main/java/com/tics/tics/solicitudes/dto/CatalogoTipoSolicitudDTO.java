package com.tics.tics.solicitudes.dto;

public class CatalogoTipoSolicitudDTO {
    private Integer idTipo;
    private String nombre;
    private String descripcion;

    public Integer getIdTipo() { return idTipo; }
    public void setIdTipo(Integer idTipo) { this.idTipo = idTipo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}