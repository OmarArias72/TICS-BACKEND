package com.tics.tics.solicitudes.service;

import com.tics.tics.solicitudes.dto.CatalogoTipoSolicitudDTO;
import com.tics.tics.solicitudes.entity.CatalogoTipoSolicitud;
import com.tics.tics.solicitudes.repository.CatalogoTipoSolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogoTipoSolicitudService {

    private final CatalogoTipoSolicitudRepository tipoRepository;

    @Transactional(readOnly = true)
    public List<CatalogoTipoSolicitudDTO> obtenerTiposActivos() {
        return tipoRepository.findByActivoTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CatalogoTipoSolicitudDTO mapToDTO(CatalogoTipoSolicitud entity) {
        CatalogoTipoSolicitudDTO dto = new CatalogoTipoSolicitudDTO();
        dto.setIdTipo(entity.getIdTipo());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }
}