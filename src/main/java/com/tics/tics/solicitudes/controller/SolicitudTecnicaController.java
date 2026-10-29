package com.tics.tics.solicitudes.controller;

import com.tics.tics.solicitudes.dto.*;
import com.tics.tics.solicitudes.service.SolicitudTecnicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-tecnicas")
@RequiredArgsConstructor
public class SolicitudTecnicaController {

    private final SolicitudTecnicaService solicitudService;

    // ==========================================
    // --- ENDPOINTS DEL ALUMNO ---
    // ==========================================

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<?> obtenerMisSolicitudes(@RequestParam Integer idUsuario) {
        try {
            return ResponseEntity.ok(solicitudService.obtenerMisSolicitudes(idUsuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearSolicitud(@RequestParam Integer idUsuario, @RequestBody CrearSolicitudRequest request) {
        try {
            SolicitudTecnicaResponseDTO nueva = solicitudService.crearSolicitud(request, idUsuario);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Aquí se atraparán los errores del Anti-Spam (429 Too Many Requests lógico)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reenviar")
    public ResponseEntity<?> reenviarSolicitud(@PathVariable Integer id, @RequestParam Integer idUsuario, @RequestBody CrearSolicitudRequest request) {
        try {
            return ResponseEntity.ok(solicitudService.reabrirSolicitud(id, request, idUsuario));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/formulario")
    public ResponseEntity<?> llenarFormulario(@PathVariable Integer id, @RequestParam Integer idUsuario, @RequestBody LlenarFormularioRequest request) {
        try {
            return ResponseEntity.ok(solicitudService.llenarFormulario(id, request, idUsuario));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarSolicitud(@PathVariable Integer id, @RequestParam Integer idUsuario) {
        try {
            return ResponseEntity.ok(solicitudService.cancelarSolicitud(id, idUsuario));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================
    // --- ENDPOINTS DEL ADMIN / OPERADOR ---
    // ==========================================

    @GetMapping("/admin/panel")
    public ResponseEntity<?> obtenerPanelAdmin(@RequestParam(defaultValue = "TODAS") String filtro) {
        try {
            return ResponseEntity.ok(solicitudService.obtenerSolicitudesAdmin(filtro));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/admin/{id}/aprobar")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable Integer id, @RequestParam Integer idAdmin) {
        try {
            return ResponseEntity.ok(solicitudService.revisarSolicitud(id, true, null, idAdmin));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/admin/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Integer id, @RequestParam Integer idAdmin, @RequestBody RechazarSolicitudRequest request) {
        try {
            return ResponseEntity.ok(solicitudService.revisarSolicitud(id, false, request.getMotivoRechazo(), idAdmin));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/admin/{id}/bloquear")
    public ResponseEntity<?> bloquearParaTicket(@PathVariable Integer id, @RequestParam Integer idAdmin) {
        try {
            return ResponseEntity.ok(solicitudService.bloquearSolicitud(id, idAdmin));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/admin/{id}/capturar-ticket")
    public ResponseEntity<?> capturarTicket(@PathVariable Integer id, @RequestParam Integer idAdmin, @RequestBody CapturarTicketRequest request) {
        try {
            return ResponseEntity.ok(solicitudService.capturarTicketCreado(id, request.getCodigoTicketCreado(), idAdmin));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/admin/{id}/resolver-ticket")
    public ResponseEntity<?> resolverTicket(@PathVariable Integer id, @RequestParam Integer idAdmin, @RequestBody ResolverTicketRequest request) {
        try {
            return ResponseEntity.ok(solicitudService.resolverTicket(id, request.getCodigoTicketResuelto(), idAdmin));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrarSolicitud(@PathVariable Integer id, @RequestParam Integer idUsuario) {
        try {
            return ResponseEntity.ok(solicitudService.cerrarSolicitud(id, idUsuario));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}