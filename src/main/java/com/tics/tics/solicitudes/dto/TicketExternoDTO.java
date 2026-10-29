package com.tics.tics.solicitudes.dto;

import java.time.LocalDateTime;

public class TicketExternoDTO {
    private String codigoTicketCreado;
    private String codigoTicketResuelto;
    private LocalDateTime fechaCreacionTicket;
    private LocalDateTime fechaResolucionTicket;
    private String capturadoPorNombre;

    // Getters y Setters
    public String getCodigoTicketCreado() { return codigoTicketCreado; }
    public void setCodigoTicketCreado(String codigoTicketCreado) { this.codigoTicketCreado = codigoTicketCreado; }
    public String getCodigoTicketResuelto() { return codigoTicketResuelto; }
    public void setCodigoTicketResuelto(String codigoTicketResuelto) { this.codigoTicketResuelto = codigoTicketResuelto; }
    public LocalDateTime getFechaCreacionTicket() { return fechaCreacionTicket; }
    public void setFechaCreacionTicket(LocalDateTime fechaCreacionTicket) { this.fechaCreacionTicket = fechaCreacionTicket; }
    public LocalDateTime getFechaResolucionTicket() { return fechaResolucionTicket; }
    public void setFechaResolucionTicket(LocalDateTime fechaResolucionTicket) { this.fechaResolucionTicket = fechaResolucionTicket; }
    public String getCapturadoPorNombre() { return capturadoPorNombre; }
    public void setCapturadoPorNombre(String capturadoPorNombre) { this.capturadoPorNombre = capturadoPorNombre; }
}