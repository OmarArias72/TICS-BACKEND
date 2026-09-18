package com.tics.tics.config;

import com.tics.tics.inventario.entity.Articulo;
import com.tics.tics.inventario.entity.Equipo;
import com.tics.tics.inventario.entity.Marca;
import com.tics.tics.inventario.entity.Modelo;
import com.tics.tics.inventario.repository.ArticuloRepository;
import com.tics.tics.inventario.repository.EquipoRepository;
import com.tics.tics.inventario.repository.MarcaRepository;
import com.tics.tics.inventario.repository.ModeloRepository;
import com.tics.tics.salas.entity.Sala;
import com.tics.tics.salas.repository.SalaRepository;
import com.tics.tics.seguridad.entity.Rol;
import com.tics.tics.seguridad.entity.Usuario;
import com.tics.tics.seguridad.repository.RolRepository;
import com.tics.tics.seguridad.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final SalaRepository salaRepository;
    private final ArticuloRepository articuloRepository;
    private final MarcaRepository marcaRepository;
    private final ModeloRepository modeloRepository;
    private final EquipoRepository equipoRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (rolRepository.count() > 0) {
            System.out.println(">>> DataSeeder: La base de datos ya contiene datos. Omitiendo siembra inicial.");
            return;
        }

        System.out.println(">>> DataSeeder: Iniciando la siembra de datos de prueba...");

        // 1. Poblar Roles
        Rol rolAdmin = rolRepository.save(new Rol("Admin", "Administrador del sistema con acceso total"));
        Rol rolOperador = rolRepository.save(new Rol("Operador-tics", "Personal del área de TICS con acceso a inventarios y mantenimiento"));
        Rol rolProfesor = rolRepository.save(new Rol("Profesor", "Docente con permisos para reservar salas de cómputo"));

        // 2. Poblar Usuarios
        Usuario adminUser = new Usuario();
        adminUser.setNombre("Omar");
        adminUser.setApellidos("Arias Domínguez");
        adminUser.setCorreo("admin@uaemex.mx");
        adminUser.setPasswordHash("Admin123!");
        adminUser.setRol(rolAdmin);
        adminUser.setActivo(true);
        usuarioRepository.save(adminUser);

        Usuario operadorUser = new Usuario();
        operadorUser.setNombre("Carlos");
        operadorUser.setApellidos("Sánchez");
        operadorUser.setCorreo("operador.tics@uaemex.mx");
        operadorUser.setPasswordHash("Operador123!");
        operadorUser.setRol(rolOperador);
        operadorUser.setActivo(true);
        usuarioRepository.save(operadorUser);

        Usuario profesorUser = new Usuario();
        profesorUser.setNombre("Dra. Roberto");
        profesorUser.setApellidos("Gómez");
        profesorUser.setCorreo("profesor@uaemex.mx");
        profesorUser.setPasswordHash("Profesor123!");
        profesorUser.setRol(rolProfesor);
        profesorUser.setActivo(true);
        usuarioRepository.save(profesorUser);

        // 3. Poblar Salas de Cómputo
        Sala sala1 = salaRepository.save(new Sala("CC-01", "Centro de Cómputo 1", "Edificio A, Planta Baja"));
        Sala sala2 = salaRepository.save(new Sala("CC-02", "Laboratorio de Redes", "Edificio B, Piso 1"));

        // 4. Poblar Catálogos de Inventario
        Articulo laptopArt = articuloRepository.save(new Articulo("LAPTOP"));
        Articulo pcArt = articuloRepository.save(new Articulo("COMPUTADORA DE ESCRITORIO"));

        Marca marcaDell = marcaRepository.save(new Marca("DELL"));
        Marca marcaHp = marcaRepository.save(new Marca("HEWLETT-PACKARD"));

        Modelo modeloLatitude = modeloRepository.save(new Modelo(marcaDell, "LATITUDE 3420"));
        Modelo modeloProDesk = modeloRepository.save(new Modelo(marcaHp, "PRO 6300"));

        // 5. Poblar Equipos Iniciales
        Equipo eq1 = new Equipo();
        eq1.setInventario("INV-2026-001");
        eq1.setSerie("SN12345678");
        eq1.setArticulo(laptopArt);
        eq1.setModelo(modeloLatitude);
        eq1.setCantidad(1);
        eq1.setCaracteristicas("Core i5 11va Gen, 16GB RAM, 512GB SSD");
        eq1.setEstado("ALTA");
        equipoRepository.save(eq1);

        Equipo eq2 = new Equipo();
        eq2.setInventario("INV-2026-002");
        eq2.setSerie("SN87654321");
        eq2.setArticulo(pcArt);
        eq2.setModelo(modeloProDesk);
        eq2.setCantidad(1);
        eq2.setCaracteristicas("Core i7 12va Gen, 32GB RAM, 1TB SSD");
        eq2.setEstado("ALTA");
        equipoRepository.save(eq2);

        System.out.println(">>> DataSeeder: Siembra de datos completada exitosamente.");
    }
}