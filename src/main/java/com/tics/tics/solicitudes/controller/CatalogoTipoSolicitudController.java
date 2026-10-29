package com.tics.tics.solicitudes.controller;

import com.tics.tics.solicitudes.dto.CatalogoTipoSolicitudDTO;
import com.tics.tics.solicitudes.service.CatalogoTipoSolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-tecnicas/catalogos")
@RequiredArgsConstructor
public class CatalogoTipoSolicitudController {

    private final CatalogoTipoSolicitudService catalogoService;

    @GetMapping("/tipos")
    public ResponseEntity<List<CatalogoTipoSolicitudDTO>> listarTipos() {
        return ResponseEntity.ok(catalogoService.obtenerTiposActivos());
    }
}