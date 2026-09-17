/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tics.tics.controllers;

/**
 *
 * @author omar_arias12
 */

import com.tics.tics.dtos.BusquedaRequest;
import com.tics.tics.dtos.EquipoMovilCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.tics.tics.modelos.Equipo;
import com.tics.tics.repositorios.EquipoRepository;
import com.tics.tics.servicios.InventoryService;

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
            return ResponseEntity.ok(equipo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- Endpoints de Búsqueda Modificados ---

    @PostMapping("/buscar-por-serie")
    public ResponseEntity<List<Equipo>> buscarPorSerie(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(equipoRepository.findBySerieContainingIgnoreCase(request.getTermino()));
    }

    @PostMapping("/buscar-por-marca")
    public ResponseEntity<List<Equipo>> buscarPorMarca(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(equipoRepository.findDistinctByModelo_Marca_NombreMarcaContainingIgnoreCase(request.getTermino()));
    }

    @PostMapping("/buscar-por-modelo")
    public ResponseEntity<List<Equipo>> buscarPorModelo(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(equipoRepository.findByModelo_NombreModeloContainingIgnoreCase(request.getTermino()));
    }

    @PostMapping("/buscar-por-articulo")
    public ResponseEntity<List<Equipo>> buscarPorArticulo(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(equipoRepository.findByArticulo_NombreArticuloContainingIgnoreCase(request.getTermino()));
    }
    
    @GetMapping("/equipos")
    public ResponseEntity<List<Equipo>> listarEquipos() {
        return ResponseEntity.ok(equipoRepository.findAll());
    }
}
