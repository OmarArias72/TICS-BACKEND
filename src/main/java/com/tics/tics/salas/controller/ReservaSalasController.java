package com.tics.tics.salas.controller;

import com.tics.tics.salas.dto.*;
import com.tics.tics.salas.service.ReservaSalasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas-salas")
@RequiredArgsConstructor
public class ReservaSalasController {

    private final ReservaSalasService reservaSalasService;

    // --- Docentes / Usuarios Públicos ---

    @GetMapping("/agenda")
    public ResponseEntity<List<UsoSalaResponseDTO>> obtenerAgenda(
            @RequestParam Integer idSala,
            @RequestParam(required = false) String fecha) {
        return ResponseEntity.ok(reservaSalasService.obtenerAgendaPorSalaYFecha(idSala, fecha));
    }

    @PostMapping("/reservar")
    public ResponseEntity<?> registrarReservaDirecta(@RequestBody ReservaDirectaRequest request) {
        try {
            UsoSalaResponseDTO uso = reservaSalasService.registrarReservaDirecta(request);
            return new ResponseEntity<>(uso, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/solicitud-extra")
    public ResponseEntity<?> registrarSolicitudExtraordinaria(@RequestBody SolicitudExtraRequest request) {
        try {
            SolicitudReservaResponseDTO solicitud = reservaSalasService.registrarSolicitudExtraordinaria(request);
            return new ResponseEntity<>(solicitud, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- Panel Administrativo ---

    @GetMapping("/admin/solicitudes-pendientes")
    public ResponseEntity<List<SolicitudReservaResponseDTO>> listarSolicitudesPendientes() {
        return ResponseEntity.ok(reservaSalasService.listarSolicitudesPendientes());
    }

    @PostMapping("/admin/aprobar-solicitud")
    public ResponseEntity<?> aprobarSolicitud(@RequestBody AprobarSolicitudRequest request) {
        try {
            UsoSalaResponseDTO reservaGenerada = reservaSalasService.aprobarSolicitud(request);
            return ResponseEntity.ok(reservaGenerada);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/admin/rechazar-solicitud")
    public ResponseEntity<?> rechazarSolicitud(@RequestBody RechazarSolicitudRequest request) {
        try {
            SolicitudReservaResponseDTO rechazada = reservaSalasService.rechazarSolicitud(request);
            return ResponseEntity.ok(rechazada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/admin/reasignar")
    public ResponseEntity<?> reasignarReserva(@RequestBody ReasignarReservaRequest request) {
        try {
            UsoSalaResponseDTO reasignada = reservaSalasService.reasignarReserva(request);
            return ResponseEntity.ok(reasignada);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/admin/cancelar")
    public ResponseEntity<?> cancelarReserva(@RequestBody CancelarReservaRequest request) {
        try {
            UsoSalaResponseDTO cancelada = reservaSalasService.cancelarReserva(request);
            return ResponseEntity.ok(cancelada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<List<SolicitudReservaResponseDTO>> listarMisSolicitudes(
            @RequestParam Integer idUsuario) {
        return ResponseEntity.ok(reservaSalasService.listarSolicitudesDeUsuario(idUsuario));
    }


}