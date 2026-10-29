package com.tics.tics.salas.service;

import com.tics.tics.salas.dto.*;
import com.tics.tics.salas.entity.Sala;
import com.tics.tics.salas.entity.SolicitudReserva;
import com.tics.tics.salas.entity.UsoSala;
import com.tics.tics.salas.repository.SalaRepository;
import com.tics.tics.salas.repository.SolicitudReservaRepository;
import com.tics.tics.salas.repository.UsoSalaRepository;
import com.tics.tics.seguridad.entity.Usuario;
import com.tics.tics.seguridad.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaSalasService {

    private final UsoSalaRepository usoSalaRepository;
    private final SolicitudReservaRepository solicitudReservaRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<UsoSalaResponseDTO> obtenerAgendaPorSalaYFecha(Integer idSala, String fechaStr) {
        LocalDate fecha = (fechaStr != null && !fechaStr.trim().isEmpty())
                ? LocalDate.parse(fechaStr)
                : LocalDate.now();

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);

        return usoSalaRepository.findBySala_IdSalaAndFechaInicioBetweenOrderByFechaInicioAsc(idSala, inicioDia, finDia)
                .stream()
                .map(this::mapToUsoSalaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsoSalaResponseDTO registrarReservaDirecta(ReservaDirectaRequest request) {
        LocalDateTime fechaInicio = combinarFechaHora(request.getFecha(), request.getHoraInicio());
        LocalDateTime fechaFin = combinarFechaHora(request.getFecha(), request.getHoraFin());

        if (!fechaFin.isAfter(fechaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }

        if (usoSalaRepository.existeEmpalmeHorario(request.getIdSala(), fechaInicio, fechaFin)) {
            throw new IllegalStateException("El horario seleccionado entra en conflicto con una reserva existente. Utiliza el formulario de solicitud extraordinaria.");
        }

        Sala sala = salaRepository.findById(request.getIdSala())
                .orElseThrow(() -> new IllegalArgumentException("No existe la sala especificada con ID: " + request.getIdSala()));

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario solicitante con ID: " + request.getIdUsuario()));

        UsoSala uso = new UsoSala();
        uso.setSala(sala);
        uso.setUsuario(usuario);
        uso.setFechaInicio(fechaInicio);
        uso.setFechaFin(fechaFin);
        uso.setProposito(request.getProposito());
        uso.setEstado("Confirmada");
        uso.setCreadoPor(usuario);

        return mapToUsoSalaDTO(usoSalaRepository.save(uso));
    }

    @Transactional
    public SolicitudReservaResponseDTO registrarSolicitudExtraordinaria(SolicitudExtraRequest request) {
        if (request.getJustificacion() == null || request.getJustificacion().trim().isEmpty()) {
            throw new IllegalArgumentException("La justificación es requerida para una solicitud extraordinaria.");
        }

        LocalDateTime fechaInicio = combinarFechaHora(request.getFecha(), request.getHoraInicio());
        LocalDateTime fechaFin = combinarFechaHora(request.getFecha(), request.getHoraFin());

        if (!fechaFin.isAfter(fechaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }

        Sala sala = salaRepository.findById(request.getIdSala())
                .orElseThrow(() -> new IllegalArgumentException("No existe la sala especificada con ID: " + request.getIdSala()));

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario solicitante con ID: " + request.getIdUsuario()));

        SolicitudReserva solicitud = new SolicitudReserva();
        solicitud.setSala(sala);
        solicitud.setSolicitante(usuario);
        solicitud.setFechaInicio(fechaInicio);
        solicitud.setFechaFin(fechaFin);
        solicitud.setJustificacion(request.getJustificacion().trim());
        solicitud.setEstado("Pendiente");

        return mapToSolicitudDTO(solicitudReservaRepository.save(solicitud));
    }

    @Transactional(readOnly = true)
    public List<SolicitudReservaResponseDTO> listarSolicitudesPendientes() {
        return solicitudReservaRepository.findByEstadoOrderByFechaSolicitudDesc("Pendiente")
                .stream()
                .map(this::mapToSolicitudDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsoSalaResponseDTO aprobarSolicitud(AprobarSolicitudRequest request) {
        SolicitudReserva solicitud = solicitudReservaRepository.findById(request.getIdSolicitud())
                .orElseThrow(() -> new IllegalArgumentException("No existe la solicitud con ID: " + request.getIdSolicitud()));

        if (!"Pendiente".equalsIgnoreCase(solicitud.getEstado())) {
            throw new IllegalStateException("La solicitud ya ha sido procesada previamente.");
        }

        Usuario admin = usuarioRepository.findById(request.getIdAdmin())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario administrador con ID: " + request.getIdAdmin()));

        String fechaBaseStr = solicitud.getFechaInicio().toLocalDate().toString();
        LocalDateTime fechaInicio = combinarFechaHora(fechaBaseStr, request.getHoraInicio());
        LocalDateTime fechaFin = combinarFechaHora(fechaBaseStr, request.getHoraFin());

        Sala salaDestino = salaRepository.findById(request.getIdSalaDestino())
                .orElseThrow(() -> new IllegalArgumentException("No existe la sala de destino seleccionada con ID: " + request.getIdSalaDestino()));

        if (usoSalaRepository.existeEmpalmeHorario(salaDestino.getIdSala(), fechaInicio, fechaFin)) {
            throw new IllegalStateException("La sala elegida presenta traslape de horarios para la franja seleccionada.");
        }

        UsoSala uso = new UsoSala();
        uso.setSala(salaDestino);
        uso.setUsuario(solicitud.getSolicitante());
        uso.setFechaInicio(fechaInicio);
        uso.setFechaFin(fechaFin);
        uso.setProposito(solicitud.getJustificacion());
        uso.setEstado("Confirmada");
        uso.setIdSolicitudRelacionada(solicitud.getIdSolicitud());
        uso.setCreadoPor(admin);
        uso.setAdminModifico(admin);

        UsoSala usoGuardado = usoSalaRepository.save(uso);

        solicitud.setEstado("Aprobada");
        solicitud.setAdminAtendio(admin);
        solicitud.setUsoGenerado(usoGuardado);
        solicitudReservaRepository.save(solicitud);

        return mapToUsoSalaDTO(usoGuardado);
    }

    @Transactional
    public SolicitudReservaResponseDTO rechazarSolicitud(RechazarSolicitudRequest request) {
        if (request.getMotivoRechazo() == null || request.getMotivoRechazo().trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio.");
        }

        SolicitudReserva solicitud = solicitudReservaRepository.findById(request.getIdSolicitud())
                .orElseThrow(() -> new IllegalArgumentException("No existe la solicitud con ID: " + request.getIdSolicitud()));

        Usuario admin = usuarioRepository.findById(request.getIdAdmin())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario administrador con ID: " + request.getIdAdmin()));

        solicitud.setEstado("Rechazada");
        solicitud.setMotivoRechazo(request.getMotivoRechazo().trim());
        solicitud.setAdminAtendio(admin);

        return mapToSolicitudDTO(solicitudReservaRepository.save(solicitud));
    }

    @Transactional
    public UsoSalaResponseDTO reasignarReserva(ReasignarReservaRequest request) {
        UsoSala uso = usoSalaRepository.findById(request.getIdUso())
                .orElseThrow(() -> new IllegalArgumentException("No existe el registro de reserva con ID: " + request.getIdUso()));

        Usuario admin = usuarioRepository.findById(request.getIdAdmin())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario administrador con ID: " + request.getIdAdmin()));

        String fechaBaseStr = (request.getFecha() != null && !request.getFecha().trim().isEmpty())
                ? request.getFecha().trim()
                : uso.getFechaInicio().toLocalDate().toString();

        LocalDateTime nuevaFechaInicio = combinarFechaHora(fechaBaseStr, request.getHoraInicio());
        LocalDateTime nuevaFechaFin = combinarFechaHora(fechaBaseStr, request.getHoraFin());

        Sala salaDestino = salaRepository.findById(request.getIdSalaDestino())
                .orElseThrow(() -> new IllegalArgumentException("No existe la sala de destino seleccionada con ID: " + request.getIdSalaDestino()));

        if (usoSalaRepository.existeEmpalmeHorarioExcluyendoActual(salaDestino.getIdSala(), uso.getIdUso(), nuevaFechaInicio, nuevaFechaFin)) {
            throw new IllegalStateException("Empalme de horario detectado en la sala destino para los nuevos horarios.");
        }

        uso.setSala(salaDestino);
        uso.setFechaInicio(nuevaFechaInicio);
        uso.setFechaFin(nuevaFechaFin);
        uso.setEstado("Reasignada");
        uso.setAdminModifico(admin);

        return mapToUsoSalaDTO(usoSalaRepository.save(uso));
    }

    @Transactional
    public UsoSalaResponseDTO cancelarReserva(CancelarReservaRequest request) {
        if (request.getMotivoCancelacion() == null || request.getMotivoCancelacion().trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de cancelación es obligatorio.");
        }

        UsoSala uso = usoSalaRepository.findById(request.getIdUso())
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva con ID: " + request.getIdUso()));

        Usuario admin = usuarioRepository.findById(request.getIdAdmin())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario administrador con ID: " + request.getIdAdmin()));

        uso.setEstado("Cancelada");
        uso.setMotivoCancelacion(request.getMotivoCancelacion().trim());
        uso.setAdminModifico(admin);

        return mapToUsoSalaDTO(usoSalaRepository.save(uso));
    }

    private LocalDateTime combinarFechaHora(String fechaStr, String horaStr) {
        LocalDate fecha = LocalDate.parse(fechaStr);
        LocalTime hora = LocalTime.parse(horaStr);
        return LocalDateTime.of(fecha, hora);
    }

    private UsoSalaResponseDTO mapToUsoSalaDTO(UsoSala uso) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        UsoSalaResponseDTO dto = new UsoSalaResponseDTO();
        dto.setIdUso(uso.getIdUso());
        dto.setIdSala(uso.getSala().getIdSala());
        dto.setCodigoSala(uso.getSala().getCodigoSala());
        dto.setNombreSala(uso.getSala().getNombre());
        dto.setProfesor(uso.getUsuario().getNombre() + (uso.getUsuario().getApellidos() != null ? " " + uso.getUsuario().getApellidos() : ""));
        dto.setHoraInicio(uso.getFechaInicio().format(timeFormatter));
        dto.setHoraFin(uso.getFechaFin().format(timeFormatter));
        dto.setProposito(uso.getProposito());
        dto.setEstado(uso.getEstado());
        dto.setFechaRegistro(uso.getFechaRegistro());
        return dto;
    }

    private SolicitudReservaResponseDTO mapToSolicitudDTO(SolicitudReserva s) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        SolicitudReservaResponseDTO dto = new SolicitudReservaResponseDTO();
        dto.setIdSolicitud(s.getIdSolicitud());
        dto.setIdSala(s.getSala().getIdSala());
        dto.setCodigoSala(s.getSala().getCodigoSala());
        dto.setNombreSala(s.getSala().getNombre());
        dto.setProfesor(s.getSolicitante().getNombre() + (s.getSolicitante().getApellidos() != null ? " " + s.getSolicitante().getApellidos() : ""));
        dto.setFecha(s.getFechaInicio().toLocalDate().toString());
        dto.setHoraInicio(s.getFechaInicio().format(timeFormatter));
        dto.setHoraFin(s.getFechaFin().format(timeFormatter));
        dto.setJustificacion(s.getJustificacion());
        dto.setEstado(s.getEstado());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<SolicitudReservaResponseDTO> listarSolicitudesDeUsuario(Integer idUsuario) {
        return solicitudReservaRepository
                .findBySolicitante_IdUsuarioOrderByFechaSolicitudDesc(idUsuario)
                .stream()
                .map(this::mapToSolicitudDTO)
                .collect(Collectors.toList());
    }


}