package com.tics.tics.solicitudes.entity;

import com.tics.tics.seguridad.entity.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets_externos")
public class TicketExterno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket")
    private Integer idTicket;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_solicitud", nullable = false, unique = true)
    private SolicitudTecnica solicitud;

    @Column(name = "codigo_ticket_creado", length = 50)
    private String codigoTicketCreado;

    @Column(name = "codigo_ticket_resuelto", length = 50)
    private String codigoTicketResuelto;

    @Column(name = "correo_asunto_creacion", columnDefinition = "TEXT")
    private String correoAsuntoCreacion;

    @Column(name = "correo_cuerpo_creacion", columnDefinition = "TEXT")
    private String correoCuerpoCreacion;

    @Column(name = "correo_asunto_resolucion", columnDefinition = "TEXT")
    private String correoAsuntoResolucion;

    @Column(name = "correo_cuerpo_resolucion", columnDefinition = "TEXT")
    private String correoCuerpoResolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capturado_por")
    private Usuario capturadoPor;

    @Column(name = "fecha_creacion_ticket")
    private LocalDateTime fechaCreacionTicket;

    @Column(name = "fecha_resolucion_ticket")
    private LocalDateTime fechaResolucionTicket;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public TicketExterno() {}

    public Integer getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Integer idTicket) {
        this.idTicket = idTicket;
    }

    public SolicitudTecnica getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SolicitudTecnica solicitud) {
        this.solicitud = solicitud;
    }

    public String getCodigoTicketCreado() {
        return codigoTicketCreado;
    }

    public void setCodigoTicketCreado(String codigoTicketCreado) {
        this.codigoTicketCreado = codigoTicketCreado;
    }

    public String getCodigoTicketResuelto() {
        return codigoTicketResuelto;
    }

    public void setCodigoTicketResuelto(String codigoTicketResuelto) {
        this.codigoTicketResuelto = codigoTicketResuelto;
    }

    public String getCorreoAsuntoCreacion() {
        return correoAsuntoCreacion;
    }

    public void setCorreoAsuntoCreacion(String correoAsuntoCreacion) {
        this.correoAsuntoCreacion = correoAsuntoCreacion;
    }

    public String getCorreoCuerpoCreacion() {
        return correoCuerpoCreacion;
    }

    public void setCorreoCuerpoCreacion(String correoCuerpoCreacion) {
        this.correoCuerpoCreacion = correoCuerpoCreacion;
    }

    public String getCorreoAsuntoResolucion() {
        return correoAsuntoResolucion;
    }

    public void setCorreoAsuntoResolucion(String correoAsuntoResolucion) {
        this.correoAsuntoResolucion = correoAsuntoResolucion;
    }

    public String getCorreoCuerpoResolucion() {
        return correoCuerpoResolucion;
    }

    public void setCorreoCuerpoResolucion(String correoCuerpoResolucion) {
        this.correoCuerpoResolucion = correoCuerpoResolucion;
    }

    public Usuario getCapturadoPor() {
        return capturadoPor;
    }

    public void setCapturadoPor(Usuario capturadoPor) {
        this.capturadoPor = capturadoPor;
    }

    public LocalDateTime getFechaCreacionTicket() {
        return fechaCreacionTicket;
    }

    public void setFechaCreacionTicket(LocalDateTime fechaCreacionTicket) {
        this.fechaCreacionTicket = fechaCreacionTicket;
    }

    public LocalDateTime getFechaResolucionTicket() {
        return fechaResolucionTicket;
    }

    public void setFechaResolucionTicket(LocalDateTime fechaResolucionTicket) {
        this.fechaResolucionTicket = fechaResolucionTicket;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}