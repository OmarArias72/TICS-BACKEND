package com.tics.tics.inventario.controller;

import com.tics.tics.inventario.dto.BusquedaRequest;
import com.tics.tics.inventario.dto.ConteoInventarioDTO;
import com.tics.tics.inventario.dto.EquipoMovilCreate;
import com.tics.tics.inventario.dto.EquipoVistaDTO;
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
            EquipoVistaDTO equipo = inventoryService.crearEquipoDesdeMovil(data);
            return new ResponseEntity<>(equipo, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el equipo: " + e.getMessage());
        }
    }



    @GetMapping("/equipos")
    public ResponseEntity<List<EquipoVistaDTO>> listarEquipos() {
        return ResponseEntity.ok(inventoryService.obtenerTodosLosEquipos());
    }

    @PostMapping("/buscar-por-serie")
    public ResponseEntity<List<EquipoVistaDTO>> buscarPorSerie(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(inventoryService.buscarPorSerie(request.getQuery()));
    }

    @PostMapping("/buscar-por-marca")
    public ResponseEntity<List<EquipoVistaDTO>> buscarPorMarca(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(inventoryService.buscarPorMarca(request.getQuery()));
    }

    @PostMapping("/buscar-por-modelo")
    public ResponseEntity<List<EquipoVistaDTO>> buscarPorModelo(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(inventoryService.buscarPorModelo(request.getQuery()));
    }

    @PostMapping("/buscar-por-articulo")
    public ResponseEntity<List<EquipoVistaDTO>> buscarPorArticulo(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(inventoryService.buscarPorArticulo(request.getQuery()));
    }

    @PostMapping("/buscar-global")
    public ResponseEntity<List<EquipoVistaDTO>> buscarGlobal(@RequestBody BusquedaRequest request) {
        return ResponseEntity.ok(inventoryService.buscarEquipos(request.getQuery()));
    }

    @PatchMapping("/estado-masivo")
    public ResponseEntity<?> actualizarEstadoMasivo(@RequestBody com.tics.tics.inventario.dto.ActualizarEstadoMasivoRequest request) {
        try {
            List<EquipoVistaDTO> equiposActualizados = inventoryService.actualizarEstadoEquiposMasivo(
                    request.getInventarios(),
                    request.getNuevoEstado()
            );
            return ResponseEntity.ok(equiposActualizados);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la actualización masiva: " + e.getMessage());
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearEquipo(@RequestBody com.tics.tics.inventario.dto.EquipoCreateRequest request) {
        try {
            EquipoVistaDTO nuevoEquipo = inventoryService.crearEquipoCompleto(request);
            return new ResponseEntity<>(nuevoEquipo, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el equipo y sus asociaciones: " + e.getMessage());
        }
    }

    @PutMapping("/{inventario}")
    public ResponseEntity<?> actualizarEquipo(
            @PathVariable String inventario,
            @RequestBody com.tics.tics.inventario.dto.EquipoCreateRequest request) {
        try {
            EquipoVistaDTO actualizado = inventoryService.actualizarEquipo(inventario, request);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el equipo: " + e.getMessage());
        }
    }

    @DeleteMapping("/{inventario}")
    public ResponseEntity<?> eliminarEquipo(@PathVariable String inventario) {
        try {
            inventoryService.eliminarEquipo(inventario);
            return ResponseEntity.noContent().build(); // Devuelve HTTP 204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el equipo: " + e.getMessage());
        }
    }

    /**
     * Conteo de inventario agrupado por artículo o modelo, con desglose por estado.
     * @param agruparPor "articulo" (default) o "modelo"
     */
    @GetMapping("/conteo")
    public ResponseEntity<List<ConteoInventarioDTO>> contarInventario(
            @RequestParam(defaultValue = "articulo") String agruparPor) {
        List<ConteoInventarioDTO> resultado = "modelo".equalsIgnoreCase(agruparPor)
                ? inventoryService.contarPorModelo()
                : inventoryService.contarPorArticulo();
        return ResponseEntity.ok(resultado);
    }

}