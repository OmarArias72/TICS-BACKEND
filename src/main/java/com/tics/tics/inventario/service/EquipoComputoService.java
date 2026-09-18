package com.tics.tics.inventario.service;

import com.tics.tics.inventario.dto.EquipoComputoRequest;
import com.tics.tics.inventario.entity.Equipo;
import com.tics.tics.inventario.entity.EquipoComputo;
import com.tics.tics.inventario.repository.EquipoComputoRepository;
import com.tics.tics.inventario.repository.EquipoRepository;
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

    @Transactional(readOnly = true)
    public List<EquipoComputo> obtenerTodos() {
        return equipoComputoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EquipoComputo obtenerPorId(Integer id) {
        return equipoComputoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el detalle del equipo de cómputo con ID: " + id));
    }

    @Transactional(readOnly = true)
    public EquipoComputo obtenerPorInventario(String inventario) {
        return equipoComputoRepository.findByEquipo_Inventario(inventario)
                .orElseThrow(() -> new IllegalArgumentException("No existe registro técnico para el inventario: " + inventario));
    }

    @Transactional(readOnly = true)
    public List<EquipoComputo> listarPorSala(Integer idSala) {
        return equipoComputoRepository.findBySala_IdSala(idSala);
    }

    @Transactional
    public EquipoComputo guardarOCargarDetalleTecnico(EquipoComputoRequest request) {
        if (request.getInventario() == null || request.getInventario().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de inventario del equipo base es obligatorio.");
        }

        String inventarioLimpio = request.getInventario().trim();

        Equipo equipoBase = equipoRepository.findById(inventarioLimpio)
                .orElseThrow(() -> new IllegalArgumentException("No existe un equipo registrado con el inventario: " + inventarioLimpio));

        EquipoComputo equipoComputo = equipoComputoRepository.findByEquipo_Inventario(inventarioLimpio)
                .orElseGet(() -> {
                    EquipoComputo nuevo = new EquipoComputo();
                    nuevo.setEquipo(equipoBase);
                    return nuevo;
                });

        if (request.getIdSala() != null) {
            Sala sala = salaRepository.findById(request.getIdSala())
                    .orElseThrow(() -> new IllegalArgumentException("No existe la sala especificada con ID: " + request.getIdSala()));
            equipoComputo.setSala(sala);
        } else {
            equipoComputo.setSala(null);
        }

        equipoComputo.setEspecificacionesHardware(request.getEspecificacionesHardware());
        equipoComputo.setSistemaOperativo(request.getSistemaOperativo());
        equipoComputo.setSoftwareInstalado(request.getSoftwareInstalado());
        equipoComputo.setEstadoTeclado(request.getEstadoTeclado());
        equipoComputo.setEstadoMouse(request.getEstadoMouse());
        equipoComputo.setEstadoCableEthernet(request.getEstadoCableEthernet());
        equipoComputo.setEstadoMonitor(request.getEstadoMonitor());
        equipoComputo.setProcesador(request.getProcesador());
        equipoComputo.setRam(request.getRam());
        equipoComputo.setAlmacenamiento(request.getAlmacenamiento());
        equipoComputo.setContrasena(request.getContrasena());
        equipoComputo.setEstadoEquipo(request.getEstadoEquipo());
        equipoComputo.setEstadoComputadora(request.getEstadoComputadora());
        equipoComputo.setObservaciones(request.getObservaciones());
        equipoComputo.setUltimoUsuarioActualizacion(request.getUltimoUsuarioActualizacion());

        return equipoComputoRepository.save(equipoComputo);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!equipoComputoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe el registro técnico con ID: " + id);
        }
        equipoComputoRepository.deleteById(id);
    }
}