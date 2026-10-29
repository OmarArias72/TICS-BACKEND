package com.tics.tics.solicitudes.service;

import com.tics.tics.solicitudes.dto.*;
import com.tics.tics.solicitudes.entity.*;
import com.tics.tics.solicitudes.repository.*;
import com.tics.tics.seguridad.entity.Usuario;
import com.tics.tics.seguridad.repository.UsuarioRepository;
import com.tics.tics.solicitudes.utils.CryptoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudTecnicaService {

    private final SolicitudTecnicaRepository solicitudRepository;
    private final CatalogoTipoSolicitudRepository tipoRepository;
    private final FormularioDetalleRepository formularioRepository;
    private final TicketExternoRepository ticketRepository;
    private final HistorialSolicitudRepository historialRepository;
    private final UsuarioRepository usuarioRepository;

    // ==========================================
    // 1. CREAR SOLICITUD (Alumno) - CON ANTI-SPAM
    // ==========================================
    @Transactional
    public SolicitudTecnicaResponseDTO crearSolicitud(CrearSolicitudRequest request, Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar Anti-saturación: Límite de 3 activas
        long activas = solicitudRepository.countSolicitudesActivasByUsuario(idUsuario);
        if (activas >= 3) {
            throw new IllegalStateException("Límite alcanzado: Solo puedes tener 3 solicitudes activas al mismo tiempo.");
        }

        CatalogoTipoSolicitud tipo = tipoRepository.findById(request.getIdTipo())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de solicitud inválido"));

        // Validar Anti-saturación: Cooldown de 24 horas por tipo
        solicitudRepository.findFirstByUsuario_IdUsuarioAndTipo_IdTipoOrderByFechaSolicitudDesc(idUsuario, tipo.getIdTipo())
                .ifPresent(ultima -> {
                    if (ultima.getFechaSolicitud().plusHours(24).isAfter(LocalDateTime.now())) {
                        throw new IllegalStateException("Debes esperar 24 horas para enviar otra solicitud del mismo tipo.");
                    }
                });

        // Si la descripción es para el tipo "OTRO", validar que no venga vacía
        if (tipo.getNombre().equals("OTRO") && (request.getMotivo() == null || request.getMotivo().trim().isEmpty())) {
            throw new IllegalArgumentException("El motivo es obligatorio para el tipo 'OTRO'.");
        }

        SolicitudTecnica solicitud = new SolicitudTecnica();
        solicitud.setUsuario(usuario);
        solicitud.setTipo(tipo);
        solicitud.setMotivo(request.getMotivo());
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        solicitud = solicitudRepository.save(solicitud);
        registrarHistorial(solicitud, usuario, null, EstadoSolicitud.PENDIENTE, "Solicitud creada por el alumno.");

        return mapToDTO(solicitud, true);
    }

    // ==========================================
    // 2. REENVIAR SOLICITUD (Tras rechazo)
    // ==========================================
    @Transactional
    public SolicitudTecnicaResponseDTO reabrirSolicitud(Integer idSolicitudOriginal, CrearSolicitudRequest request, Integer idUsuario) {
        SolicitudTecnica original = solicitudRepository.findById(idSolicitudOriginal)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (original.getUsuario().getIdUsuario() != idUsuario || original.getEstado() != EstadoSolicitud.RECHAZADA) {
            throw new IllegalStateException("No puedes reabrir esta solicitud.");
        }

        original.setMotivo(request.getMotivo());
        original.setEstado(EstadoSolicitud.PENDIENTE);
        original.setMotivoRechazo(null);

        original = solicitudRepository.save(original);
        registrarHistorial(original, original.getUsuario(), EstadoSolicitud.RECHAZADA, EstadoSolicitud.PENDIENTE, "Solicitud reabierta con nueva información.");

        return mapToDTO(original, true);
    }

    // ==========================================
    // 3. APROBAR / RECHAZAR (Admin/Operador)
    // ==========================================
    @Transactional
    public SolicitudTecnicaResponseDTO revisarSolicitud(Integer idSolicitud, boolean aprobar, String motivoRechazo, Integer idAdmin) {
        SolicitudTecnica solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        Usuario admin = usuarioRepository.findById(idAdmin)
                .orElseThrow(() -> new IllegalArgumentException("Admin no encontrado"));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden revisar solicitudes PENDIENTES.");
        }

        if (aprobar) {
            solicitud.setEstado(EstadoSolicitud.APROBADA);
            registrarHistorial(solicitud, admin, EstadoSolicitud.PENDIENTE, EstadoSolicitud.APROBADA, "Solicitud aprobada.");
        } else {
            if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
                throw new IllegalArgumentException("El motivo de rechazo es obligatorio.");
            }
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            solicitud.setMotivoRechazo(motivoRechazo);
            registrarHistorial(solicitud, admin, EstadoSolicitud.PENDIENTE, EstadoSolicitud.RECHAZADA, "Rechazada: " + motivoRechazo);
        }

        solicitud.setAdminRevisor(admin);
        solicitud.setFechaRevision(LocalDateTime.now());

        return mapToDTO(solicitudRepository.save(solicitud), true);
    }

    // ==========================================
    // 4. LLENAR FORMULARIO (Alumno)
    // ==========================================
    @Transactional
    public SolicitudTecnicaResponseDTO llenarFormulario(Integer idSolicitud, LlenarFormularioRequest request, Integer idUsuario) {
        SolicitudTecnica solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (solicitud.getUsuario().getIdUsuario() != idUsuario) {
            throw new IllegalStateException("No tienes permiso sobre esta solicitud.");
        }

        // Solo se puede editar si está APROBADA o si ya se envió pero el ticket no se ha creado
        if (solicitud.getEstado() != EstadoSolicitud.APROBADA && solicitud.getEstado() != EstadoSolicitud.FORMULARIO_ENVIADO) {
            throw new IllegalStateException("El formulario no está disponible para edición en este estado.");
        }

        // Validaciones estrictas
        validarFormulario(request);

        FormularioDetalle formulario = formularioRepository.findBySolicitud_IdSolicitud(idSolicitud)
                .orElseGet(FormularioDetalle::new);

        formulario.setSolicitud(solicitud);
        formulario.setNombreCompleto(request.getNombreCompleto());
        formulario.setCorreoInstitucional(request.getCorreoInstitucional());
        formulario.setCorreoAlterno(request.getCorreoAlterno());
        formulario.setNumeroCuenta(request.getNumeroCuenta());
        formulario.setTelefono(request.getTelefono());
        formulario.setDescripcionProblema(request.getDescripcionProblema());
        // Encriptar la CURP (AES)
        formulario.setCurpHash(CryptoUtil.encriptar(request.getCurp()));

        formularioRepository.save(formulario);

        if (solicitud.getEstado() == EstadoSolicitud.APROBADA) {
            solicitud.setEstado(EstadoSolicitud.FORMULARIO_ENVIADO);
            registrarHistorial(solicitud, solicitud.getUsuario(), EstadoSolicitud.APROBADA, EstadoSolicitud.FORMULARIO_ENVIADO, "Formulario de datos enviado.");
            solicitudRepository.save(solicitud);
        }

        return mapToDTO(solicitud, true);
    }

    // ==========================================
    // 5. CANCELAR SOLICITUD (Alumno)
    // ==========================================
    @Transactional
    public SolicitudTecnicaResponseDTO cancelarSolicitud(Integer idSolicitud, Integer idUsuario) {
        SolicitudTecnica solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (solicitud.getUsuario().getIdUsuario() != idUsuario) {
            throw new IllegalStateException("No tienes permiso.");
        }

        // Solo se puede cancelar si no se ha generado el ticket externo aún
        List<EstadoSolicitud> estadosCancelables = Arrays.asList(
                EstadoSolicitud.PENDIENTE, EstadoSolicitud.APROBADA, EstadoSolicitud.FORMULARIO_ENVIADO
        );
        if (!estadosCancelables.contains(solicitud.getEstado())) {
            throw new IllegalStateException("La solicitud ya está en proceso con TICS y no puede ser cancelada.");
        }

        solicitud.setEstado(EstadoSolicitud.CANCELADA);
        registrarHistorial(solicitud, solicitud.getUsuario(), solicitud.getEstado(), EstadoSolicitud.CANCELADA, "Cancelada por el alumno.");

        return mapToDTO(solicitudRepository.save(solicitud), true);
    }

    // ==========================================
    // 6. BLOQUEO OPTIMISTA Y TICKETS (Admin/Operador)
    // ==========================================
    @Transactional
    public SolicitudTecnicaResponseDTO bloquearSolicitud(Integer idSolicitud, Integer idAdmin) {
        SolicitudTecnica solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        Usuario admin = usuarioRepository.findById(idAdmin).orElseThrow();

        if (solicitud.getEstado() != EstadoSolicitud.FORMULARIO_ENVIADO) {
            throw new IllegalStateException("La solicitud no está lista para ser atendida.");
        }

        // Validar si alguien más la tiene bloqueada (con un margen de 10 min por si se cae la sesión)
        if (solicitud.getOperadorAtendiendo() != null && solicitud.getFechaBloqueo().plusMinutes(10).isAfter(LocalDateTime.now())) {
            if (solicitud.getOperadorAtendiendo().getIdUsuario() != idAdmin) {
                throw new IllegalStateException("Esta solicitud está siendo atendida por: " + solicitud.getOperadorAtendiendo().getNombre());
            }
        }

        solicitud.setEstado(EstadoSolicitud.ESPERA_TICKET);
        solicitud.setOperadorAtendiendo(admin);
        solicitud.setFechaBloqueo(LocalDateTime.now());

        registrarHistorial(solicitud, admin, EstadoSolicitud.FORMULARIO_ENVIADO, EstadoSolicitud.ESPERA_TICKET, "Tomada por " + admin.getNombre() + " para generar ticket.");
        return mapToDTO(solicitudRepository.save(solicitud), true);
    }

    @Transactional
    public SolicitudTecnicaResponseDTO capturarTicketCreado(Integer idSolicitud, String codigoTicket, Integer idAdmin) {
        SolicitudTecnica solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        Usuario admin = usuarioRepository.findById(idAdmin).orElseThrow();

        if (solicitud.getEstado() != EstadoSolicitud.ESPERA_TICKET) {
            throw new IllegalStateException("La solicitud debe estar en ESPERA_TICKET para capturar el ID.");
        }

        TicketExterno ticket = ticketRepository.findBySolicitud_IdSolicitud(idSolicitud)
                .orElseGet(TicketExterno::new);

        ticket.setSolicitud(solicitud);
        ticket.setCodigoTicketCreado(codigoTicket);
        ticket.setCapturadoPor(admin);
        ticket.setFechaCreacionTicket(LocalDateTime.now());
        ticketRepository.save(ticket);

        solicitud.setEstado(EstadoSolicitud.TICKET_CREADO);
        solicitud.setOperadorAtendiendo(null); // Liberamos el bloqueo
        solicitud.setFechaBloqueo(null);

        registrarHistorial(solicitud, admin, EstadoSolicitud.ESPERA_TICKET, EstadoSolicitud.TICKET_CREADO, "ID Externo capturado: " + codigoTicket);
        return mapToDTO(solicitudRepository.save(solicitud), true);
    }

    @Transactional
    public SolicitudTecnicaResponseDTO resolverTicket(Integer idSolicitud, String codigoResuelto, Integer idAdmin) {
        SolicitudTecnica solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        Usuario admin = usuarioRepository.findById(idAdmin).orElseThrow();

        if (solicitud.getEstado() != EstadoSolicitud.TICKET_CREADO) {
            throw new IllegalStateException("La solicitud debe estar TICKET_CREADO para marcarse como resuelta.");
        }

        TicketExterno ticket = ticketRepository.findBySolicitud_IdSolicitud(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("No existe ticket registrado."));

        ticket.setCodigoTicketResuelto(codigoResuelto);
        ticket.setFechaResolucionTicket(LocalDateTime.now());
        ticketRepository.save(ticket);

        // CAMBIO: Se pasa directamente a CERRADA porque TICS ya resolvió
        solicitud.setEstado(EstadoSolicitud.CERRADA);

        registrarHistorial(solicitud, admin, EstadoSolicitud.TICKET_CREADO, EstadoSolicitud.CERRADA, "Ticket resuelto y cerrado por TICS. ID de resolución: " + codigoResuelto);
        return mapToDTO(solicitudRepository.save(solicitud), true);
    }

    // ==========================================
    // 7. CONSULTAS (ReadOnly)
    // ==========================================
    @Transactional(readOnly = true)
    public List<SolicitudTecnicaResponseDTO> obtenerMisSolicitudes(Integer idUsuario) {
        return solicitudRepository.findByUsuario_IdUsuarioOrderByFechaSolicitudDesc(idUsuario)
                .stream()
                .map(s -> mapToDTO(s, true)) // true = con historial
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudTecnicaResponseDTO> obtenerSolicitudesAdmin(String filtro) {
        List<SolicitudTecnica> lista;
        switch (filtro.toUpperCase()) {
            case "PENDIENTES":
                lista = solicitudRepository.findByEstadoOrderByFechaSolicitudAsc(EstadoSolicitud.PENDIENTE);
                break;
            case "SIN_ID":
                lista = solicitudRepository.findByEstadoInOrderByFechaSolicitudAsc(
                        Arrays.asList(EstadoSolicitud.APROBADA, EstadoSolicitud.FORMULARIO_ENVIADO, EstadoSolicitud.ESPERA_TICKET));
                break;
            default: // TODAS
                lista = solicitudRepository.findAllByOrderByFechaSolicitudDesc();
                break;
        }
        return lista.stream().map(s -> mapToDTO(s, false)).collect(Collectors.toList());
    }

    // ==========================================
    // UTILIDADES Y VALIDACIONES
    // ==========================================
    private void validarFormulario(LlenarFormularioRequest req) {
        if (req.getNumeroCuenta() == null || !req.getNumeroCuenta().matches("\\d{7}")) {
            throw new IllegalArgumentException("El número de cuenta debe tener exactamente 7 dígitos.");
        }
        if (req.getCorreoInstitucional() == null || !req.getCorreoInstitucional().endsWith("@uaemex.mx")) {
            throw new IllegalArgumentException("El correo institucional debe terminar en @uaemex.mx");
        }
        if (req.getCurp() == null || !req.getCurp().matches("^[A-Z]{4}\\d{6}[HM][A-Z]{5}[A-Z0-9]\\d$")) {
            throw new IllegalArgumentException("El formato de la CURP es inválido.");
        }
    }

    private String hashCurp(String curp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(curp.trim().toUpperCase().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la CURP", e);
        }
    }

    private void registrarHistorial(SolicitudTecnica solicitud, Usuario usuario, EstadoSolicitud anterior, EstadoSolicitud nuevo, String comentario) {
        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setSolicitud(solicitud);
        historial.setUsuarioAccion(usuario);
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(nuevo);
        historial.setComentario(comentario);
        historialRepository.save(historial);
    }

    // MAPPER: Transforma Entidad a DTO
    private SolicitudTecnicaResponseDTO mapToDTO(SolicitudTecnica s, boolean cargarHistorial) {
        SolicitudTecnicaResponseDTO dto = new SolicitudTecnicaResponseDTO();
        dto.setIdSolicitud(s.getIdSolicitud());
        dto.setNombreUsuario(s.getUsuario().getNombre() + " " + (s.getUsuario().getApellidos() != null ? s.getUsuario().getApellidos() : ""));
        dto.setCorreoUsuario(s.getUsuario().getCorreo());
        dto.setTipoNombre(s.getTipo().getNombre());
        dto.setMotivo(s.getMotivo());
        dto.setEstado(s.getEstado().name());
        dto.setMotivoRechazo(s.getMotivoRechazo());
        dto.setFechaSolicitud(s.getFechaSolicitud());
        dto.setFechaRevision(s.getFechaRevision());

        if (s.getAdminRevisor() != null) {
            dto.setRevisorNombre(s.getAdminRevisor().getNombre());
        }

        // Cargar Formulario si existe
        formularioRepository.findBySolicitud_IdSolicitud(s.getIdSolicitud()).ifPresent(f -> {
            FormularioDetalleDTO fDto = new FormularioDetalleDTO();
            fDto.setIdFormulario(f.getIdFormulario());
            fDto.setNombreCompleto(f.getNombreCompleto());
            fDto.setCorreoInstitucional(f.getCorreoInstitucional());
            fDto.setCorreoAlterno(f.getCorreoAlterno());
            fDto.setNumeroCuenta(f.getNumeroCuenta());
            fDto.setTelefono(f.getTelefono());
            fDto.setDescripcionProblema(f.getDescripcionProblema());

            // Desencriptar la CURP para que el Admin la vea
            if (f.getCurpHash() != null) {
                fDto.setCurp(CryptoUtil.desencriptar(f.getCurpHash()));
            }

            dto.setFormulario(fDto);
        });

        if (cargarHistorial) {
            List<HistorialSolicitudDTO> hist = historialRepository.findBySolicitud_IdSolicitudOrderByFechaCambioDesc(s.getIdSolicitud())
                    .stream().map(h -> {
                        HistorialSolicitudDTO hDto = new HistorialSolicitudDTO();
                        hDto.setIdHistorial(h.getIdHistorial());
                        hDto.setEstadoAnterior(h.getEstadoAnterior() != null ? h.getEstadoAnterior().name() : null);
                        hDto.setEstadoNuevo(h.getEstadoNuevo().name());
                        hDto.setComentario(h.getComentario());
                        hDto.setFechaCambio(h.getFechaCambio());
                        hDto.setUsuarioAccionNombre(h.getUsuarioAccion().getNombre());
                        return hDto;
                    }).collect(Collectors.toList());
            dto.setHistorial(hist);
        }

        return dto;
    }

    @Transactional
    public SolicitudTecnicaResponseDTO cerrarSolicitud(Integer idSolicitud, Integer idUsuario) {
        SolicitudTecnica sol = solicitudRepository.findById(idSolicitud).orElseThrow();
        if (sol.getUsuario().getIdUsuario() != idUsuario || sol.getEstado() != EstadoSolicitud.RESUELTO) {
            throw new IllegalStateException("No puedes cerrar esta solicitud.");
        }
        sol.setEstado(EstadoSolicitud.CERRADA);
        registrarHistorial(sol, sol.getUsuario(), EstadoSolicitud.RESUELTO, EstadoSolicitud.CERRADA, "El alumno confirmó la resolución.");
        return mapToDTO(solicitudRepository.save(sol), true);
    }
}