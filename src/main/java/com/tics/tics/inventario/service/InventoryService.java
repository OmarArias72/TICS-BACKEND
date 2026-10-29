package com.tics.tics.inventario.service;

import com.tics.tics.inventario.dto.ConteoInventarioDTO;
import com.tics.tics.inventario.dto.EquipoCreateRequest;
import com.tics.tics.inventario.dto.EquipoMovilCreate;
import com.tics.tics.inventario.dto.EquipoVistaDTO;
import com.tics.tics.inventario.entity.Articulo;
import com.tics.tics.inventario.entity.BitacoraEquipo;
import com.tics.tics.inventario.entity.Equipo;
import com.tics.tics.inventario.entity.Marca;
import com.tics.tics.inventario.entity.Modelo;
import com.tics.tics.inventario.repository.ArticuloRepository;
import com.tics.tics.inventario.repository.BitacoraEquipoRepository;
import com.tics.tics.inventario.repository.EquipoRepository;
import com.tics.tics.inventario.repository.MarcaRepository;
import com.tics.tics.inventario.repository.ModeloRepository;
import com.tics.tics.seguridad.entity.Usuario;
import com.tics.tics.seguridad.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ArticuloRepository articuloRepo;
    private final MarcaRepository marcaRepo;
    private final ModeloRepository modeloRepo;
    private final EquipoRepository equipoRepo;
    private final BitacoraEquipoRepository bitacoraRepo;
    private final UsuarioRepository usuarioRepo;

    // --- Mapeador DTO ---
    private EquipoVistaDTO mapToDTO(Equipo equipo) {
        EquipoVistaDTO dto = new EquipoVistaDTO();
        dto.setInventario(equipo.getInventario());
        dto.setSerie(equipo.getSerie());
        dto.setNombreArticulo(equipo.getArticulo() != null ? equipo.getArticulo().getNombreArticulo() : "Desconocido");
        dto.setMarca(equipo.getModelo() != null && equipo.getModelo().getMarca() != null ? equipo.getModelo().getMarca().getNombreMarca() : "Desconocida");
        dto.setModelo(equipo.getModelo() != null ? equipo.getModelo().getNombreModelo() : "Desconocido");
        dto.setCantidad(equipo.getCantidad());
        dto.setCaracteristicas(equipo.getCaracteristicas());
        dto.setEstado(equipo.getEstado());
        dto.setFechaRegistro(equipo.getFechaRegistro());
        dto.setFechaActualizacion(equipo.getFechaActualizacion());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<EquipoVistaDTO> obtenerTodosLosEquipos() {
        return equipoRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipoVistaDTO> buscarEquipos(String query) {
        if (query == null || query.trim().isEmpty()) {
            return obtenerTodosLosEquipos();
        }
        return equipoRepo.searchByGlobalTerm(query.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipoVistaDTO> buscarPorSerie(String serie) {
        if (serie == null || serie.trim().isEmpty()) return obtenerTodosLosEquipos();
        return equipoRepo.findBySerieContainingIgnoreCase(serie.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipoVistaDTO> buscarPorMarca(String marca) {
        if (marca == null || marca.trim().isEmpty()) return obtenerTodosLosEquipos();
        return equipoRepo.findDistinctByModelo_Marca_NombreMarcaContainingIgnoreCase(marca.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipoVistaDTO> buscarPorModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) return obtenerTodosLosEquipos();
        return equipoRepo.findByModelo_NombreModeloContainingIgnoreCase(modelo.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EquipoVistaDTO> buscarPorArticulo(String articulo) {
        if (articulo == null || articulo.trim().isEmpty()) return obtenerTodosLosEquipos();
        return equipoRepo.findByArticulo_NombreArticuloContainingIgnoreCase(articulo.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EquipoVistaDTO crearEquipoDesdeMovil(EquipoMovilCreate data) {
        if (data.getInventario() == null || data.getInventario().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de inventario es obligatorio");
        }

        String inventarioLimpio = data.getInventario().trim();

        if (equipoRepo.existsById(inventarioLimpio)) {
            throw new IllegalStateException("El equipo con inventario " + inventarioLimpio + " ya existe");
        }

        String nombreMarcaStr = (data.getNombreMarca() != null && !data.getNombreMarca().trim().isEmpty())
                ? data.getNombreMarca().trim().toUpperCase()
                : "GENÉRICO";

        Marca marca = marcaRepo.findByNombreMarcaIgnoreCase(nombreMarcaStr)
                .orElseGet(() -> marcaRepo.save(new Marca(nombreMarcaStr)));

        String nombreModeloStr = (data.getNombreModelo() != null && !data.getNombreModelo().trim().isEmpty())
                ? data.getNombreModelo().trim().toUpperCase()
                : "GENÉRICO";

        Modelo modelo = modeloRepo.findByNombreModeloIgnoreCaseAndMarca_IdMarca(nombreModeloStr, marca.getIdMarca())
                .orElseGet(() -> modeloRepo.save(new Modelo(marca, nombreModeloStr)));

        String nombreArticuloStr = (data.getNombreArticulo() != null && !data.getNombreArticulo().trim().isEmpty())
                ? data.getNombreArticulo().trim().toUpperCase()
                : "ARTÍCULO DESCONOCIDO";

        Articulo articulo = articuloRepo.findByNombreArticuloIgnoreCase(nombreArticuloStr)
                .orElseGet(() -> articuloRepo.save(new Articulo(nombreArticuloStr)));

        Equipo equipo = new Equipo();
        equipo.setInventario(inventarioLimpio);
        equipo.setSerie(data.getSerie() != null ? data.getSerie().trim() : null);
        equipo.setArticulo(articulo);
        equipo.setModelo(modelo);
        equipo.setCantidad(data.getCantidad() != null ? data.getCantidad() : 1);
        equipo.setCaracteristicas(data.getCaracteristicas());
        equipo.setEstado((data.getEstado() != null && !data.getEstado().trim().isEmpty())
                ? data.getEstado().trim().toUpperCase() : "ALTA");

        Equipo equipoGuardado = equipoRepo.save(equipo);

        // Registro de Auditoría en Bitácora
        if (data.getIdUsuario() != null) {
            Usuario usuario = usuarioRepo.findById(data.getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario de auditoría con ID: " + data.getIdUsuario()));

            BitacoraEquipo bitacora = new BitacoraEquipo();
            bitacora.setEquipo(equipoGuardado);
            bitacora.setUsuario(usuario);
            bitacora.setAccion("ALTA_MOVIL");
            bitacora.setCampoModificado("TODOS");
            bitacora.setValorAnterior(null);
            bitacora.setValorNuevo("Equipo registrado desde dispositivo móvil con estado: " + equipoGuardado.getEstado());
            bitacora.setObservacion(data.getObservacion() != null ? data.getObservacion() : "Registro vía app móvil");

            bitacoraRepo.save(bitacora);
        }

        return mapToDTO(equipoGuardado);
    }



    @Transactional(readOnly = true)
    public List<EquipoVistaDTO> filtrarParaReporte(String fechaInicioStr, String fechaFinStr, String articulo) {
        // Establecer fechas por defecto si no se envían (ej. desde el inicio de los tiempos hasta hoy)
        LocalDateTime fechaInicio = (fechaInicioStr != null && !fechaInicioStr.isEmpty())
                ? java.time.LocalDate.parse(fechaInicioStr).atStartOfDay()
                : LocalDateTime.of(2000, 1, 1, 0, 0);

        LocalDateTime fechaFin = (fechaFinStr != null && !fechaFinStr.isEmpty())
                ? java.time.LocalDate.parse(fechaFinStr).atTime(java.time.LocalTime.MAX)
                : LocalDateTime.now();

        List<Equipo> resultados;

        // Decidir qué método usar dependiendo de si se envió el artículo
        if (articulo != null && !articulo.trim().isEmpty()) {
            resultados = equipoRepo.buscarPorRangoFechasYArticulo(fechaInicio, fechaFin, articulo.trim());
        } else {
            resultados = equipoRepo.buscarPorRangoFechas(fechaInicio, fechaFin);
        }

        return resultados.stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public List<EquipoVistaDTO> actualizarEstadoEquiposMasivo(List<String> inventarios, String nuevoEstado) {
        if (inventarios == null || inventarios.isEmpty()) {
            throw new IllegalArgumentException("La lista de inventarios no puede estar vacía.");
        }
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio.");
        }

        String estadoEstandarizado = nuevoEstado.trim().toUpperCase();

        // Obtener todos los equipos de la base de datos de una sola vez
        List<Equipo> equipos = equipoRepo.findAllById(inventarios);

        if (equipos.size() != inventarios.size()) {
            throw new IllegalArgumentException("Uno o más números de inventario proporcionados no existen en el sistema.");
        }

        // Actualizar el estado de todos los equipos en memoria
        for (Equipo equipo : equipos) {
            equipo.setEstado(estadoEstandarizado);
        }

        // Guardar todos los cambios en lote (batch update)
        List<Equipo> actualizados = equipoRepo.saveAll(equipos);

        return actualizados.stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public EquipoVistaDTO crearEquipoCompleto(EquipoCreateRequest data) {
        if (data.getInventario() == null || data.getInventario().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de inventario es obligatorio.");
        }

        String inventarioLimpio = data.getInventario().trim();

        if (equipoRepo.existsById(inventarioLimpio)) {
            throw new IllegalStateException("El equipo con el número de inventario " + inventarioLimpio + " ya existe.");
        }

        // 1. Gestionar Marca (Buscar o Crear)
        String nombreMarca = (data.getNombreMarca() != null && !data.getNombreMarca().trim().isEmpty())
                ? data.getNombreMarca().trim().toUpperCase()
                : "GENÉRICA";
        Marca marca = marcaRepo.findByNombreMarcaIgnoreCase(nombreMarca)
                .orElseGet(() -> marcaRepo.save(new Marca(nombreMarca)));

        // 2. Gestionar Modelo (Buscar o Crear asociado a la Marca correspondiente)
        String nombreModelo = (data.getNombreModelo() != null && !data.getNombreModelo().trim().isEmpty())
                ? data.getNombreModelo().trim().toUpperCase()
                : "GENÉRICO";
        Modelo modelo = modeloRepo.findByNombreModeloIgnoreCaseAndMarca_IdMarca(nombreModelo, marca.getIdMarca())
                .orElseGet(() -> modeloRepo.save(new Modelo(marca, nombreModelo)));

        // 3. Gestionar Artículo (Buscar o Crear)
        String nombreArticulo = (data.getNombreArticulo() != null && !data.getNombreArticulo().trim().isEmpty())
                ? data.getNombreArticulo().trim().toUpperCase()
                : "ARTÍCULO GENERAL";
        Articulo articulo = articuloRepo.findByNombreArticuloIgnoreCase(nombreArticulo)
                .orElseGet(() -> articuloRepo.save(new Articulo(nombreArticulo)));

        // 4. Registrar el Equipo vinculando todas las entidades y sus características
        Equipo equipo = new Equipo();
        equipo.setInventario(inventarioLimpio);
        equipo.setSerie(data.getSerie() != null ? data.getSerie().trim() : null);
        equipo.setArticulo(articulo);
        equipo.setModelo(modelo);
        equipo.setCantidad(data.getCantidad() != null ? data.getCantidad() : 1);
        equipo.setCaracteristicas(data.getCaracteristicas());
        equipo.setEstado(data.getEstado() != null && !data.getEstado().trim().isEmpty()
                ? data.getEstado().trim().toUpperCase()
                : "ALTA");

        Equipo equipoGuardado = equipoRepo.save(equipo);

        return mapToDTO(equipoGuardado);
    }

    @Transactional
    public EquipoVistaDTO actualizarEquipo(String inventario, EquipoCreateRequest data) {
        Equipo equipo = equipoRepo.findById(inventario)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el equipo con inventario: " + inventario));

        // 1. Gestionar Marca si se proporciona
        if (data.getNombreMarca() != null && !data.getNombreMarca().trim().isEmpty()) {
            String nombreMarca = data.getNombreMarca().trim().toUpperCase();
            Marca marca = marcaRepo.findByNombreMarcaIgnoreCase(nombreMarca)
                    .orElseGet(() -> marcaRepo.save(new Marca(nombreMarca)));

            // Gestionar Modelo asociado
            if (data.getNombreModelo() != null && !data.getNombreModelo().trim().isEmpty()) {
                String nombreModelo = data.getNombreModelo().trim().toUpperCase();
                Modelo modelo = modeloRepo.findByNombreModeloIgnoreCaseAndMarca_IdMarca(nombreModelo, marca.getIdMarca())
                        .orElseGet(() -> modeloRepo.save(new Modelo(marca, nombreModelo)));
                equipo.setModelo(modelo);
            }
        } else if (data.getNombreModelo() != null && !data.getNombreModelo().trim().isEmpty()) {
            String nombreModelo = data.getNombreModelo().trim().toUpperCase();
            Modelo modelo = modeloRepo.findByNombreModeloIgnoreCaseAndMarca_IdMarca(nombreModelo, equipo.getModelo().getMarca().getIdMarca())
                    .orElseGet(() -> modeloRepo.save(new Modelo(equipo.getModelo().getMarca(), nombreModelo)));
            equipo.setModelo(modelo);
        }

        // 2. Gestionar Artículo si se proporciona
        if (data.getNombreArticulo() != null && !data.getNombreArticulo().trim().isEmpty()) {
            String nombreArticulo = data.getNombreArticulo().trim().toUpperCase();
            Articulo articulo = articuloRepo.findByNombreArticuloIgnoreCase(nombreArticulo)
                    .orElseGet(() -> articuloRepo.save(new Articulo(nombreArticulo)));
            equipo.setArticulo(articulo);
        }

        // 3. Actualizar campos restantes
        if (data.getSerie() != null) {
            equipo.setSerie(data.getSerie().trim());
        }
        if (data.getCantidad() != null) {
            equipo.setCantidad(data.getCantidad());
        }
        if (data.getCaracteristicas() != null) {
            equipo.setCaracteristicas(data.getCaracteristicas());
        }
        if (data.getEstado() != null && !data.getEstado().trim().isEmpty()) {
            equipo.setEstado(data.getEstado().trim().toUpperCase());
        }

        Equipo equipoActualizado = equipoRepo.save(equipo);
        return mapToDTO(equipoActualizado);
    }

    @Transactional
    public void eliminarEquipo(String inventario) {
        if (!equipoRepo.existsById(inventario)) {
            throw new IllegalArgumentException("No se encontró el equipo con inventario: " + inventario);
        }
        equipoRepo.deleteById(inventario);
    }

    // ==================== CONTEOS POR AGRUPACIÓN ====================

    @Transactional(readOnly = true)
    public List<ConteoInventarioDTO> contarPorArticulo() {
        return agregarConteos(equipoRepo.contarPorArticuloYEstado(), false);
    }

    @Transactional(readOnly = true)
    public List<ConteoInventarioDTO> contarPorModelo() {
        return agregarConteos(equipoRepo.contarPorModeloYEstado(), true);
    }

    /**
     * Transforma las filas [categoria, estado, count] o [modelo, marca, estado, count]
     * en una lista de ConteoInventarioDTO con desglose por estado.
     */
    private List<ConteoInventarioDTO> agregarConteos(List<Object[]> rows, boolean porModelo) {
        // LinkedHashMap mantiene el orden alfabético que ya viene del ORDER BY
        Map<String, ConteoInventarioDTO> map = new LinkedHashMap<>();

        for (Object[] row : rows) {
            String categoria;
            String estado;
            Long count;

            if (porModelo) {
                String modelo = (String) row[0];
                String marca  = (String) row[1];
                categoria = (marca != null ? marca : "SIN MARCA") + " - " +
                        (modelo != null ? modelo : "SIN MODELO");
                estado = (String) row[2];
                count = ((Number) row[3]).longValue();
            } else {
                categoria = (String) row[0];
                if (categoria == null) categoria = "SIN ARTÍCULO";
                estado = (String) row[1];
                count = ((Number) row[2]).longValue();
            }

            ConteoInventarioDTO dto = map.computeIfAbsent(
                    categoria, ConteoInventarioDTO::new);

            dto.setTotal(dto.getTotal() + count);

            if (estado != null) {
                switch (estado.toUpperCase()) {
                    case "ALTA" -> dto.setAlta(dto.getAlta() + count);
                    case "BAJA" -> dto.setBaja(dto.getBaja() + count);
                    case "MANTENIMIENTO" -> dto.setMantenimiento(dto.getMantenimiento() + count);
                }
            }
        }

        return new ArrayList<>(map.values());
    }
}