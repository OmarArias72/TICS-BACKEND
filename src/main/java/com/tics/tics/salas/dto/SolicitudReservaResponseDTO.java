package com.tics.tics.salas.dto;

import com.tics.tics.salas.entity.SolicitudReserva;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class SolicitudReservaResponseDTO {
    private Integer idSolicitud;
    private Integer idSala;
    private String codigoSala;
    private String nombreSala;

    private Integer idSolicitante;
    private String profesor;

    private String fecha;
    private String horaInicio;
    private String horaFin;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private String justificacion;
    private String estado;

    private String motivoRechazo;
    private String adminAtendio;
    private Integer idUsoGenerado;

    private LocalDateTime fechaSolicitud;

    private SolicitudReservaResponseDTO mapToSolicitudDTO(SolicitudReserva s) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        SolicitudReservaResponseDTO dto = new SolicitudReservaResponseDTO();
        dto.setIdSolicitud(s.getIdSolicitud());

        // Sala
        dto.setIdSala(s.getSala().getIdSala());
        dto.setCodigoSala(s.getSala().getCodigoSala());
        dto.setNombreSala(s.getSala().getNombre());

        // Solicitante
        dto.setIdSolicitante(s.getSolicitante().getIdUsuario());
        String nombreCompleto = s.getSolicitante().getNombre()
                + (s.getSolicitante().getApellidos() != null
                ? " " + s.getSolicitante().getApellidos()
                : "");
        dto.setProfesor(nombreCompleto);

        // Fechas / horas
        dto.setFecha(s.getFechaInicio().toLocalDate().toString());
        dto.setHoraInicio(s.getFechaInicio().format(timeFormatter));
        dto.setHoraFin(s.getFechaFin().format(timeFormatter));
        dto.setFechaInicio(s.getFechaInicio());
        dto.setFechaFin(s.getFechaFin());

        // Contenido
        dto.setJustificacion(s.getJustificacion());
        dto.setEstado(s.getEstado());
        dto.setMotivoRechazo(s.getMotivoRechazo());

        // Admin que atendió
        if (s.getAdminAtendio() != null) {
            String adminNombre = s.getAdminAtendio().getNombre()
                    + (s.getAdminAtendio().getApellidos() != null
                    ? " " + s.getAdminAtendio().getApellidos()
                    : "");
            dto.setAdminAtendio(adminNombre);
        }

        // Uso generado (si fue aprobada)
        if (s.getUsoGenerado() != null) {
            dto.setIdUsoGenerado(s.getUsoGenerado().getIdUso());
        }

        dto.setFechaSolicitud(s.getFechaSolicitud());

        return dto;
    }
}