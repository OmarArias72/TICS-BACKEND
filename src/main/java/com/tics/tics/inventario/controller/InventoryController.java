package com.tics.tics.inventario.controller;

import com.tics.tics.inventario.dto.BusquedaRequest;
import com.tics.tics.inventario.dto.EquipoMovilCreate;
import com.tics.tics.inventario.entity.Equipo;
import com.tics.tics.inventario.repository.EquipoRepository;
import com.tics.tics.inventario.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final EquipoRepository equipoRepository;

    @PostMapping("/crear-equipo-movil")
    public ResponseEntity<?> crearEquipoMovil(@RequestBody EquipoMovilCreate data) {
        try {
            Equipo equipo = inventoryService.crearEquipoDesdeMovil(data);
            return new ResponseEntity<>(equipo, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el equipo: " + e.getMessage());
        }
    }

    @PostMapping("/buscar-por-serie")
    public ResponseEntity<List<Equipo>> buscarPorSerie(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(equipoRepository.findBySerieContainingIgnoreCase(request.getQuery()));
    }

    @PostMapping("/buscar-por-marca")
    public ResponseEntity<List<Equipo>> buscarPorMarca(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(equipoRepository.findDistinctByModelo_Marca_NombreMarcaContainingIgnoreCase(request.getQuery()));
    }

    @PostMapping("/buscar-por-modelo")
    public ResponseEntity<List<Equipo>> buscarPorModelo(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(equipoRepository.findByModelo_NombreModeloContainingIgnoreCase(request.getQuery()));
    }

    @PostMapping("/buscar-por-articulo")
    public ResponseEntity<List<Equipo>> buscarPorArticulo(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(equipoRepository.findByArticulo_NombreArticuloContainingIgnoreCase(request.getQuery()));
    }

    @PostMapping("/buscar-global")
    public ResponseEntity<List<Equipo>> buscarGlobal(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(inventoryService.buscarEquipos(request.getQuery()));
    }

    @GetMapping("/equipos")
    public ResponseEntity<List<Equipo>> listarEquipos() {
        return ResponseEntity.ok(inventoryService.obtenerTodosLosEquipos());
    }
}