/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tics.tics.servicios;

/**
 *
 * @author omar_arias12
 */


import com.tics.tics.dtos.EquipoMovilCreate;
import com.tics.tics.modelos.Articulo;
import com.tics.tics.modelos.Equipo;
import com.tics.tics.modelos.Marca;
import com.tics.tics.modelos.Modelo;
import com.tics.tics.repositorios.ArticuloRepository;
import com.tics.tics.repositorios.EquipoRepository;
import com.tics.tics.repositorios.MarcaRepository;
import com.tics.tics.repositorios.ModeloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ArticuloRepository articuloRepo;
    private final MarcaRepository marcaRepo;
    private final ModeloRepository modeloRepo;
    private final EquipoRepository equipoRepo;

    public Equipo crearEquipoDesdeMovil(EquipoMovilCreate data) {
        if (equipoRepo.existsById(data.getNoInventario())) {
            throw new RuntimeException("El equipo con inventario " + data.getNoInventario() + " ya existe");
        }

        // Obtener o crear Marca
        Marca marca = marcaRepo.findByNombreMarcaIgnoreCase(data.getMarca())
                .orElseGet(() -> {
                    Marca m = new Marca();
                    m.setNombreMarca(data.getMarca().toUpperCase());
                    return marcaRepo.save(m);
                });

        // Obtener o crear Modelo
        Modelo modelo = modeloRepo.findByNombreModeloIgnoreCaseAndMarca_IdMarca(data.getModelo(), marca.getIdMarca())
                .orElseGet(() -> {
                    Modelo mod = new Modelo();
                    mod.setNombreModelo(data.getModelo().toUpperCase());
                    mod.setMarca(marca);
                    return modeloRepo.save(mod);
                });

        // Obtener o crear Artículo (Por defecto)
        Articulo articulo = articuloRepo.findByNombreArticuloIgnoreCase("ARTÍCULO DESCONOCIDO")
                .orElseGet(() -> {
                    Articulo art = new Articulo();
                    art.setNombreArticulo("ARTÍCULO DESCONOCIDO");
                    return articuloRepo.save(art);
                });

        // Crear Equipo
        Equipo equipo = new Equipo();
        equipo.setInventario(data.getNoInventario());
        equipo.setSerie(data.getNoSerie());
        equipo.setArticulo(articulo);
        equipo.setModelo(modelo);
        equipo.setCantidad(1);

        return equipoRepo.save(equipo);
    }
}