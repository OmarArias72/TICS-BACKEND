package com.tics.tics.inventario.service;

import com.tics.tics.inventario.dto.EquipoMovilCreate;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ArticuloRepository articuloRepo;
    private final MarcaRepository marcaRepo;
    private final ModeloRepository modeloRepo;
    private final EquipoRepository equipoRepo;
    private final BitacoraEquipoRepository bitacoraRepo;
    private final UsuarioRepository usuarioRepo;

    @Transactional(readOnly = true)
    public List<Equipo> obtenerTodosLosEquipos() {
        return equipoRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Equipo> buscarEquipos(String query) {
        if (query == null || query.trim().isEmpty()) {
            return equipoRepo.findAll();
        }
        return equipoRepo.searchByGlobalTerm(query.trim());
    }

    @Transactional
    public Equipo crearEquipoDesdeMovil(EquipoMovilCreate data) {
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

        return equipoGuardado;
    }
}