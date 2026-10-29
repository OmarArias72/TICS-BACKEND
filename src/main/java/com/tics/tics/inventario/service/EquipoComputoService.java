package com.tics.tics.inventario.service;

import com.tics.tics.inventario.dto.EquipoComputoRequest;
import com.tics.tics.inventario.entity.Articulo;
import com.tics.tics.inventario.entity.Equipo;
import com.tics.tics.inventario.entity.EquipoComputo;
import com.tics.tics.inventario.entity.Marca;
import com.tics.tics.inventario.entity.Modelo;
import com.tics.tics.inventario.repository.ArticuloRepository;
import com.tics.tics.inventario.repository.EquipoComputoRepository;
import com.tics.tics.inventario.repository.EquipoRepository;
import com.tics.tics.inventario.repository.MarcaRepository;
import com.tics.tics.inventario.repository.ModeloRepository;
import com.tics.tics.salas.entity.Sala;
import com.tics.tics.salas.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipoComputoService {

    private final EquipoComputoRepository equipoComputoRepository;
    private final EquipoRepository equipoRepository;
    private final SalaRepository salaRepository;
    private final ArticuloRepository articuloRepository;
    private final MarcaRepository marcaRepository;
    private final ModeloRepository modeloRepository;

    // ==================== Consultas ====================

    @Transactional(readOnly = true)
    public List<EquipoComputo> obtenerTodos() {
        return equipoComputoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EquipoComputo obtenerPorId(Integer id) {
        return equipoComputoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el detalle del equipo de cómputo con ID: " + id));
    }

    @Transactional(readOnly = true)
    public EquipoComputo obtenerPorInventario(String inventario) {
        return equipoComputoRepository.findByEquipo_Inventario(inventario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe registro técnico para el inventario: " + inventario));
    }

    @Transactional(readOnly = true)
    public List<EquipoComputo> listarPorSala(Integer idSala) {
        return equipoComputoRepository.findBySala_IdSala(idSala);
    }

    // ==================== Crear / Actualizar ====================

    @Transactional
    public EquipoComputo guardar(EquipoComputoRequest request) {
        validarRequest(request);

        String inventarioLimpio = request.getInventario().trim();

        // --- 1. Resolver o crear Artículo ---
        Articulo articulo = articuloRepository
                .findByNombreArticuloIgnoreCase(request.getNombreArticulo().trim())
                .orElseGet(() -> articuloRepository.save(
                        new Articulo(request.getNombreArticulo().trim())));

        // --- 2. Resolver o crear Marca ---
        Marca marca = marcaRepository
                .findByNombreMarcaIgnoreCase(request.getNombreMarca().trim())
                .orElseGet(() -> marcaRepository.save(
                        new Marca(request.getNombreMarca().trim())));

        // --- 3. Resolver o crear Modelo (depende de Marca) ---
        Modelo modelo = modeloRepository
                .findByNombreModeloIgnoreCaseAndMarca_IdMarca(
                        request.getNombreModelo().trim(), marca.getIdMarca())
                .orElseGet(() -> modeloRepository.save(
                        new Modelo(marca, request.getNombreModelo().trim())));

        // --- 4. Crear o actualizar Equipo (inventario = PK) ---
        Equipo equipo = equipoRepository.findById(inventarioLimpio)
                .orElseGet(Equipo::new);

        // Si es update y el inventario cambió respecto al original, lo bloqueamos
        if (request.getIdEquipoComputo() != null) {
            EquipoComputo original = equipoComputoRepository.findById(request.getIdEquipoComputo())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe el equipo de cómputo con ID: " + request.getIdEquipoComputo()));
            if (!original.getEquipo().getInventario().equalsIgnoreCase(inventarioLimpio)) {
                throw new IllegalArgumentException(
                        "No está permitido modificar el número de inventario de un equipo existente.");
            }
            equipo = original.getEquipo();
        }

        equipo.setInventario(inventarioLimpio);
        equipo.setArticulo(articulo);
        equipo.setModelo(modelo);
        equipo.setSerie(request.getSerie());
        equipo.setCantidad(request.getCantidad() != null ? request.getCantidad() : 1);
        equipo.setCaracteristicas(request.getCaracteristicas());
        equipo.setEstado(request.getEstado() != null ? request.getEstado() : "ALTA");
        equipo = equipoRepository.save(equipo);

        // --- 5. Crear o actualizar EquipoComputo ---
        EquipoComputo ec;
        if (request.getIdEquipoComputo() != null) {
            ec = equipoComputoRepository.findById(request.getIdEquipoComputo())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe el equipo de cómputo con ID: " + request.getIdEquipoComputo()));
        } else {
            ec = equipoComputoRepository.findByEquipo_Inventario(inventarioLimpio)
                    .orElseGet(EquipoComputo::new);
        }
        ec.setEquipo(equipo);

        // --- 6. Sala (opcional) ---
        if (request.getIdSala() != null) {
            Sala sala = salaRepository.findById(request.getIdSala())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe la sala especificada con ID: " + request.getIdSala()));
            ec.setSala(sala);
        } else {
            ec.setSala(null);
        }

        // --- 7. Detalle técnico ---
        ec.setEspecificacionesHardware(request.getEspecificacionesHardware());
        ec.setSistemaOperativo(request.getSistemaOperativo());
        ec.setSoftwareInstalado(request.getSoftwareInstalado());
        ec.setEstadoTeclado(request.getEstadoTeclado());
        ec.setEstadoMouse(request.getEstadoMouse());
        ec.setEstadoCableEthernet(request.getEstadoCableEthernet());
        ec.setEstadoMonitor(request.getEstadoMonitor());
        ec.setProcesador(request.getProcesador());
        ec.setRam(request.getRam());
        ec.setAlmacenamiento(request.getAlmacenamiento());

        // Contraseñas
        ec.setContrasena(request.getContrasena());
        ec.setPasswordAdmin(request.getPasswordAdmin());   // NUEVO
        ec.setLastPassword(request.getLastPassword());     // NUEVO

        // Estados (estado_equipo -> estado_cpu)
        ec.setEstadoCpu(request.getEstadoCpu());           // CAMBIO
        ec.setEstadoComputadora(request.getEstadoComputadora());

        ec.setObservaciones(request.getObservaciones());
        ec.setUltimoUsuarioActualizacion(request.getUltimoUsuarioActualizacion());

        return equipoComputoRepository.save(ec);
    }

    // ==================== Eliminar (con su Equipo base) ====================

    @Transactional
    public void eliminar(Integer id) {
        EquipoComputo ec = equipoComputoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el registro técnico con ID: " + id));

        String inventario = ec.getEquipo().getInventario();

        // El FK en BD tiene ON DELETE CASCADE, pero borramos explícitamente
        // para dejar la operación clara y atómica dentro de la transacción.
        equipoComputoRepository.delete(ec);
        equipoComputoRepository.flush();

        equipoRepository.deleteById(inventario);
    }

    // ==================== Validaciones ====================

    private void validarRequest(EquipoComputoRequest request) {
        if (request.getInventario() == null || request.getInventario().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de inventario es obligatorio.");
        }
        if (request.getNombreArticulo() == null || request.getNombreArticulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del artículo es obligatorio.");
        }
        if (request.getNombreMarca() == null || request.getNombreMarca().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio.");
        }
        if (request.getNombreModelo() == null || request.getNombreModelo().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del modelo es obligatorio.");
        }
    }
}