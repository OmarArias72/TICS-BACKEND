package com.tics.tics.solicitudes.entity;

import com.tics.tics.seguridad.entity.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_solicitudes")
public class HistorialSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private SolicitudTecnica solicitud;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_accion", nullable = false)
    private Usuario usuarioAccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 30)
    private EstadoSolicitud estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", length = 30)
    private EstadoSolicitud estadoNuevo;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_cambio", insertable = false, updatable = false)
    private LocalDateTime fechaCambio;

    public HistorialSolicitud() {}

    public Integer getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Integer idHistorial) {
        this.idHistorial = idHistorial;
    }

    public SolicitudTecnica getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SolicitudTecnica solicitud) {
        this.solicitud = solicitud;
    }

    public Usuario getUsuarioAccion() {
        return usuarioAccion;
    }

    public void setUsuarioAccion(Usuario usuarioAccion) {
        this.usuarioAccion = usuarioAccion;
    }

    public EstadoSolicitud getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoSolicitud estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoSolicitud getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoSolicitud estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }
}