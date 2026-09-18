package com.tics.tics.salas.service;

import com.tics.tics.salas.dto.SalaDTO;
import com.tics.tics.salas.entity.Sala;
import com.tics.tics.salas.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;

    @Transactional(readOnly = true)
    public List<Sala> obtenerTodas() {
        return salaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Sala obtenerPorId(Integer id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la sala con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Sala> buscar(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return salaRepository.findAll();
        }
        return salaRepository.findByNombreContainingIgnoreCaseOrUbicacionContainingIgnoreCase(termino.trim(), termino.trim());
    }

    @Transactional
    public Sala crear(SalaDTO dto) {
        if (dto.getCodigoSala() == null || dto.getCodigoSala().trim().isEmpty()) {
            throw new IllegalArgumentException("El código de sala es obligatorio.");
        }

        String codigoLimpio = dto.getCodigoSala().trim().toUpperCase();

        if (salaRepository.existsByCodigoSalaIgnoreCase(codigoLimpio)) {
            throw new IllegalStateException("Ya existe una sala registrada con el código: " + codigoLimpio);
        }

        Sala sala = new Sala();
        sala.setCodigoSala(codigoLimpio);
        sala.setNombre(dto.getNombre() != null ? dto.getNombre().trim() : null);
        sala.setUbicacion(dto.getUbicacion() != null ? dto.getUbicacion().trim() : null);
        sala.setDescripcion(dto.getDescripcion());

        return salaRepository.save(sala);
    }

    @Transactional
    public Sala actualizar(Integer id, SalaDTO dto) {
        Sala sala = obtenerPorId(id);

        if (dto.getCodigoSala() != null && !dto.getCodigoSala().trim().isEmpty()) {
            String codigoLimpio = dto.getCodigoSala().trim().toUpperCase();
            if (!sala.getCodigoSala().equalsIgnoreCase(codigoLimpio) &&
                    salaRepository.existsByCodigoSalaIgnoreCase(codigoLimpio)) {
                throw new IllegalStateException("Ya existe otra sala con el código: " + codigoLimpio);
            }
            sala.setCodigoSala(codigoLimpio);
        }

        if (dto.getNombre() != null) sala.setNombre(dto.getNombre().trim());
        if (dto.getUbicacion() != null) sala.setUbicacion(dto.getUbicacion().trim());
        if (dto.getDescripcion() != null) sala.setDescripcion(dto.getDescripcion());

        return salaRepository.save(sala);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!salaRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe la sala con ID: " + id);
        }
        salaRepository.deleteById(id);
    }
}