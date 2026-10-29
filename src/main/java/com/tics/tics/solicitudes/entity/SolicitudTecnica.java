package com.tics.tics.solicitudes.entity;

import com.tics.tics.seguridad.entity.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_tecnicas")
public class SolicitudTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_tipo", nullable = false)
    private CatalogoTipoSolicitud tipo;

    @Column(name = "motivo", nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    // Referencia en caso de reapertura
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud_origen")
    private SolicitudTecnica solicitudOrigen;

    // Auditoría de revisión
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin_revisor")
    private Usuario adminRevisor;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    // Bloqueo optimista
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_operador_atendiendo")
    private Usuario operadorAtendiendo;

    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo;

    // Fechas de control
    @Column(name = "fecha_solicitud", insertable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    public SolicitudTecnica() {}

    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public CatalogoTipoSolicitud getTipo() {
        return tipo;
    }

    public void setTipo(CatalogoTipoSolicitud tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public SolicitudTecnica getSolicitudOrigen() {
        return solicitudOrigen;
    }

    public void setSolicitudOrigen(SolicitudTecnica solicitudOrigen) {
        this.solicitudOrigen = solicitudOrigen;
    }

    public Usuario getAdminRevisor() {
        return adminRevisor;
    }

    public void setAdminRevisor(Usuario adminRevisor) {
        this.adminRevisor = adminRevisor;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public Usuario getOperadorAtendiendo() {
        return operadorAtendiendo;
    }

    public void setOperadorAtendiendo(Usuario operadorAtendiendo) {
        this.operadorAtendiendo = operadorAtendiendo;
    }

    public LocalDateTime getFechaBloqueo() {
        return fechaBloqueo;
    }

    public void setFechaBloqueo(LocalDateTime fechaBloqueo) {
        this.fechaBloqueo = fechaBloqueo;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}