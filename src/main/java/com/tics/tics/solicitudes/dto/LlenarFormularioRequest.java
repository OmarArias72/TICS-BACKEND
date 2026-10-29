package com.tics.tics.solicitudes.dto;

public class LlenarFormularioRequest {
    private String nombreCompleto;
    private String correoInstitucional;
    private String correoAlterno;
    private String numeroCuenta;
    private String curp; // Se recibe en texto plano para hashear en el servicio
    private String telefono;
    private String descripcionProblema;

    // Getters y Setters
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCorreoInstitucional() { return correoInstitucional; }
    public void setCorreoInstitucional(String correoInstitucional) { this.correoInstitucional = correoInstitucional; }
    public String getCorreoAlterno() { return correoAlterno; }
    public void setCorreoAlterno(String correoAlterno) { this.correoAlterno = correoAlterno; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    public String getCurp() { return curp; }
    public void setCurp(String curp) { this.curp = curp; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDescripcionProblema() { return descripcionProblema; }
    public void setDescripcionProblema(String descripcionProblema) { this.descripcionProblema = descripcionProblema; }
}