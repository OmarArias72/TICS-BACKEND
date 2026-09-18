package com.tics.tics.inventario.controller;

import com.tics.tics.inventario.dto.EquipoComputoRequest;
import com.tics.tics.inventario.entity.EquipoComputo;
import com.tics.tics.inventario.service.EquipoComputoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos-computo")
@RequiredArgsConstructor
public class EquipoComputoController {

    private final EquipoComputoService equipoComputoService;

    @GetMapping
    public ResponseEntity<List<EquipoComputo>> listarTodos() {
        return ResponseEntity.ok(equipoComputoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(equipoComputoService.obtenerPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/inventario/{inventario}")
    public ResponseEntity<?> obtenerPorInventario(@PathVariable String inventario) {
        try {
            return ResponseEntity.ok(equipoComputoService.obtenerPorInventario(inventario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/sala/{idSala}")
    public ResponseEntity<List<EquipoComputo>> listarPorSala(@PathVariable Integer idSala) {
        return ResponseEntity.ok(equipoComputoService.listarPorSala(idSala));
    }

    @PostMapping
    public ResponseEntity<?> guardarODetalleTecnico(@RequestBody EquipoComputoRequest request) {
        try {
            EquipoComputo guardado = equipoComputoService.guardarOCargarDetalleTecnico(request);
            return new ResponseEntity<>(guardado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el equipo de cómputo: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            equipoComputoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}