package com.tics.tics.seguridad.service;

import com.tics.tics.seguridad.dto.UsuarioAdminCreateRequest;
import com.tics.tics.seguridad.dto.UsuarioRegistroRequest;
import com.tics.tics.seguridad.dto.UsuarioResponseDTO;
import com.tics.tics.seguridad.entity.Rol;
import com.tics.tics.seguridad.entity.Usuario;
import com.tics.tics.seguridad.repository.RolRepository;
import com.tics.tics.seguridad.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario con ID: " + id));
        return mapToResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO registrarDesdeSitio(UsuarioRegistroRequest request) {
        validarCamposRegistro(request.getNombre(), request.getCorreo(), request.getPassword());

        String correoLimpio = request.getCorreo().trim().toLowerCase();

        if (usuarioRepository.existsByCorreoIgnoreCase(correoLimpio)) {
            throw new IllegalStateException("El correo electrónico " + correoLimpio + " ya se encuentra registrado.");
        }

        // Asigna por defecto el rol "Profesor"
        Rol rolProfesor = rolRepository.findByNombreRolIgnoreCase("Profesor")
                .orElseGet(() -> rolRepository.save(new Rol("Profesor", "Rol por defecto para usuarios registrados desde el sitio web")));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre().trim());
        usuario.setApellidos(request.getApellidos() != null ? request.getApellidos().trim() : null);
        usuario.setCorreo(correoLimpio);
        // Simulación de hash de contraseña (se integrará BCryptEncoder en la fase de Spring Security)
        usuario.setPasswordHash(request.getPassword());
        usuario.setRol(rolProfesor);
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);
        return mapToResponseDTO(guardado);
    }

    @Transactional
    public UsuarioResponseDTO crearDesdeAdmin(UsuarioAdminCreateRequest request) {
        validarCamposRegistro(request.getNombre(), request.getCorreo(), request.getPassword());

        if (request.getIdRol() == null) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio para la creación administrativa.");
        }

        String correoLimpio = request.getCorreo().trim().toLowerCase();

        if (usuarioRepository.existsByCorreoIgnoreCase(correoLimpio)) {
            throw new IllegalStateException("El correo electrónico " + correoLimpio + " ya existe.");
        }

        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new IllegalArgumentException("El rol con ID " + request.getIdRol() + " no existe."));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre().trim());
        usuario.setApellidos(request.getApellidos() != null ? request.getApellidos().trim() : null);
        usuario.setCorreo(correoLimpio);
        usuario.setPasswordHash(request.getPassword());
        usuario.setRol(rol);
        usuario.setActivo(request.getActivo() != null ? request.getActivo() : true);

        Usuario guardado = usuarioRepository.save(usuario);
        return mapToResponseDTO(guardado);
    }

    @Transactional
    public UsuarioResponseDTO cambiarEstadoUsuario(Integer idUsuario, Boolean activo) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario con ID: " + idUsuario));

        usuario.setActivo(activo);
        Usuario actualizado = usuarioRepository.save(usuario);
        return mapToResponseDTO(actualizado);
    }

    private void validarCamposRegistro(String nombre, String correo, String password) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
    }

    private UsuarioResponseDTO mapToResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellidos(usuario.getApellidos());
        dto.setCorreo(usuario.getCorreo());
        dto.setNombreRol(usuario.getRol().getNombreRol());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        return dto;
    }
}