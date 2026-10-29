package com.tics.tics.tics.config;

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
import com.tics.tics.salas.entity.SolicitudReserva;
import com.tics.tics.salas.entity.UsoSala;
import com.tics.tics.salas.repository.SalaRepository;
import com.tics.tics.salas.repository.SolicitudReservaRepository;
import com.tics.tics.salas.repository.UsoSalaRepository;
import com.tics.tics.seguridad.entity.Rol;
import com.tics.tics.seguridad.entity.Usuario;
import com.tics.tics.seguridad.repository.RolRepository;
import com.tics.tics.seguridad.repository.UsuarioRepository;
import com.tics.tics.solicitudes.entity.*;
import com.tics.tics.solicitudes.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    private final EquipoComputoRepository equipoComputoRepository;
    private final UsoSalaRepository usoSalaRepository;
    private final SolicitudReservaRepository solicitudReservaRepository;

    // NUEVOS REPOSITORIOS
    private final CatalogoTipoSolicitudRepository tipoSolicitudRepository;
    private final SolicitudTecnicaRepository solicitudTecnicaRepository;
    private final FormularioDetalleRepository formularioDetalleRepository;
    private final HistorialSolicitudRepository historialSolicitudRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (rolRepository.count() > 0) {
            System.out.println(">>> DataSeeder: La base de datos ya contiene datos. Omitiendo siembra inicial.");
            return;
        }

        System.out.println(">>> DataSeeder: Iniciando la siembra de datos de prueba...");

        // ==========================================================
        // 1. CATÁLOGOS BÁSICOS
        // ==========================================================
        List<Rol> roles = sembrarRoles();
        List<Usuario> usuarios = sembrarUsuarios(roles);
        List<Sala> salas = sembrarSalas();
        List<Articulo> articulos = sembrarArticulos();
        List<Marca> marcas = sembrarMarcas();
        List<Modelo> modelos = sembrarModelos(marcas);
        List<Equipo> equipos = sembrarEquipos(articulos, modelos);

        // ==========================================================
        // 2. EQUIPOS DE CÓMPUTO (detalle técnico)
        // ==========================================================
        sembrarEquiposComputo(equipos, salas, usuarios);

        // ==========================================================
        // 3. RESERVAS DE SALAS
        // ==========================================================
        sembrarReservasYRoles(salas, usuarios);

        // ==========================================================
        // 4. SOLICITUDES TÉCNICAS (MESA DE AYUDA)
        // ==========================================================
        sembrarSolicitudesTecnicas(usuarios);

        System.out.println(">>> DataSeeder: Siembra de datos completada exitosamente.");
    }

    // ==========================================================
    // 1. ROLES
    // ==========================================================
    private List<Rol> sembrarRoles() {
        Rol admin = rolRepository.save(new Rol("Admin", "Administrador del sistema con acceso total"));
        Rol operador = rolRepository.save(new Rol("Operador-tics", "Personal del área de TICS con acceso a inventarios y mantenimiento"));
        Rol profesor = rolRepository.save(new Rol("Profesor", "Docente con permisos para reservar salas de cómputo"));
        // NUEVO ROL
        Rol estudiante = rolRepository.save(new Rol("Estudiante", "Alumno con acceso a solicitar soporte técnico y reservar salas"));

        return List.of(admin, operador, profesor, estudiante);
    }

    // ==========================================================
    // 2. USUARIOS
    // ==========================================================
    private List<Usuario> sembrarUsuarios(List<Rol> roles) {
        Rol admin = roles.get(0);
        Rol operador = roles.get(1);
        Rol profesor = roles.get(2);
        Rol estudiante = roles.get(3);

        Usuario adminUser = new Usuario();
        adminUser.setNombre("Omar");
        adminUser.setApellidos("Arias Domínguez");
        adminUser.setCorreo("admin@uaemex.mx");
        adminUser.setPasswordHash("Admin123!");
        adminUser.setRol(admin);
        adminUser.setActivo(true);
        usuarioRepository.save(adminUser);

        Usuario operadorUser = new Usuario();
        operadorUser.setNombre("Carlos");
        operadorUser.setApellidos("Sánchez");
        operadorUser.setCorreo("operador.tics@uaemex.mx");
        operadorUser.setPasswordHash("Operador123!");
        operadorUser.setRol(operador);
        operadorUser.setActivo(true);
        usuarioRepository.save(operadorUser);

        Usuario profesorUser = new Usuario();
        profesorUser.setNombre("Roberto");
        profesorUser.setApellidos("Gómez");
        profesorUser.setCorreo("profesor@uaemex.mx");
        profesorUser.setPasswordHash("Profesor123!");
        profesorUser.setRol(profesor);
        profesorUser.setActivo(true);
        usuarioRepository.save(profesorUser);

        Usuario profesorUser2 = new Usuario();
        profesorUser2.setNombre("Laura");
        profesorUser2.setApellidos("Martínez Ríos");
        profesorUser2.setCorreo("laura.martinez@uaemex.mx");
        profesorUser2.setPasswordHash("Profesor123!");
        profesorUser2.setRol(profesor);
        profesorUser2.setActivo(true);
        usuarioRepository.save(profesorUser2);


        Usuario estudianteUser = new Usuario();
        estudianteUser.setNombre("Juan");
        estudianteUser.setApellidos("Pérez López");
        estudianteUser.setCorreo("alumno@uaemex.mx");
        estudianteUser.setPasswordHash("Alumno123!");
        estudianteUser.setRol(estudiante);
        estudianteUser.setActivo(true);
        usuarioRepository.save(estudianteUser);

        Usuario estudianteUser2 = new Usuario();
        estudianteUser2.setNombre("Ana");
        estudianteUser2.setApellidos("García Ruiz");
        estudianteUser2.setCorreo("ana.garcia@uaemex.mx");
        estudianteUser2.setPasswordHash("Estudiante123!");
        estudianteUser2.setRol(estudiante);
        estudianteUser2.setActivo(true);
        usuarioRepository.save(estudianteUser2);

        return List.of(adminUser, operadorUser, profesorUser, profesorUser2,estudianteUser,estudianteUser2);
    }

    // ==========================================================
    // 3. SALAS
    // ==========================================================
    private List<Sala> sembrarSalas() {
        Sala s1 = salaRepository.save(new Sala(
                "CC-01", "Centro de Cómputo 1",
                "Edificio A, Planta Baja"
        ));
        s1.setDescripcion("Sala principal de cómputo con 20 equipos de escritorio y proyector.");

        Sala s2 = salaRepository.save(new Sala(
                "CC-02", "Laboratorio de Redes",
                "Edificio B, Piso 1"
        ));
        s2.setDescripcion("Laboratorio especializado en configuración de redes y servidores.");

        Sala s3 = salaRepository.save(new Sala(
                "CC-03", "Laboratorio de Programación",
                "Edificio B, Piso 2"
        ));
        s3.setDescripcion("Sala equipada para desarrollo de software y clases de programación.");

        salaRepository.saveAll(List.of(s1, s2, s3));

        return List.of(s1, s2, s3);
    }

    // ==========================================================
    // 4. ARTÍCULOS
    // ==========================================================
    private List<Articulo> sembrarArticulos() {
        Articulo laptop = articuloRepository.save(new Articulo("LAPTOP"));
        Articulo pc = articuloRepository.save(new Articulo("COMPUTADORA DE ESCRITORIO"));
        Articulo allInOne = articuloRepository.save(new Articulo("ALL IN ONE"));
        return List.of(laptop, pc, allInOne);
    }

    // ==========================================================
    // 5. MARCAS
    // ==========================================================
    private List<Marca> sembrarMarcas() {
        Marca dell = marcaRepository.save(new Marca("DELL"));
        Marca hp = marcaRepository.save(new Marca("HEWLETT-PACKARD"));
        Marca lenovo = marcaRepository.save(new Marca("LENOVO"));
        return List.of(dell, hp, lenovo);
    }

    // ==========================================================
    // 6. MODELOS
    // ==========================================================
    private List<Modelo> sembrarModelos(List<Marca> marcas) {
        Marca dell = marcas.get(0);
        Marca hp = marcas.get(1);
        Marca lenovo = marcas.get(2);

        Modelo latitude = modeloRepository.save(new Modelo(dell, "LATITUDE 3420"));
        Modelo optiplex = modeloRepository.save(new Modelo(dell, "OPTIPLEX 3080"));
        Modelo proDesk = modeloRepository.save(new Modelo(hp, "PRO 6300"));
        Modelo thinkCentre = modeloRepository.save(new Modelo(lenovo, "THINKCENTRE M70Q"));

        return List.of(latitude, optiplex, proDesk, thinkCentre);
    }

    // ==========================================================
    // 7. EQUIPOS (base)
    // ==========================================================
    private List<Equipo> sembrarEquipos(List<Articulo> articulos, List<Modelo> modelos) {
        Articulo laptop = articulos.get(0);
        Articulo pc = articulos.get(1);
        Articulo allInOne = articulos.get(2);

        Modelo latitude = modelos.get(0);
        Modelo optiplex = modelos.get(1);
        Modelo proDesk = modelos.get(2);
        Modelo thinkCentre = modelos.get(3);

        Equipo e1 = crearEquipo("INV-2026-001", "SN12345678", laptop, latitude,
                "Core i5 11va Gen, 16GB RAM, 512GB SSD", "ALTA");
        Equipo e2 = crearEquipo("INV-2026-002", "SN87654321", pc, proDesk,
                "Core i7 12va Gen, 32GB RAM, 1TB SSD", "ALTA");
        Equipo e3 = crearEquipo("INV-2026-003", "SN11223344", pc, optiplex,
                "Core i5 10ma Gen, 8GB RAM, 256GB SSD", "ALTA");
        Equipo e4 = crearEquipo("INV-2026-004", "SN44332211", pc, optiplex,
                "Core i5 10ma Gen, 8GB RAM, 256GB SSD", "MANTENIMIENTO");
        Equipo e5 = crearEquipo("INV-2026-005", "SN55667788", allInOne, thinkCentre,
                "Core i7 11va Gen, 16GB RAM, 512GB SSD, Pantalla 23.8\"", "ALTA");

        return List.of(e1, e2, e3, e4, e5);
    }

    private Equipo crearEquipo(String inventario, String serie, Articulo articulo,
                               Modelo modelo, String caracteristicas, String estado) {
        Equipo e = new Equipo();
        e.setInventario(inventario);
        e.setSerie(serie);
        e.setArticulo(articulo);
        e.setModelo(modelo);
        e.setCantidad(1);
        e.setCaracteristicas(caracteristicas);
        e.setEstado(estado);
        return equipoRepository.save(e);
    }

    // ==========================================================
    // 8. EQUIPOS_COMPUTO (detalle técnico)
    // ==========================================================
    private void sembrarEquiposComputo(List<Equipo> equipos, List<Sala> salas, List<Usuario> usuarios) {
        Integer idAdmin = usuarios.get(0).getIdUsuario();
        Integer idOperador = usuarios.get(1).getIdUsuario();

        Sala cc01 = salas.get(0);
        Sala cc02 = salas.get(1);
        Sala cc03 = salas.get(2);

        EquipoComputo ec1 = new EquipoComputo();
        ec1.setEquipo(equipos.get(0));
        ec1.setSala(cc01);
        ec1.setEspecificacionesHardware("Laptop Dell Latitude 3420, teclado retroiluminado, HDMI, USB-C");
        ec1.setSistemaOperativo("Windows 11 Pro");
        ec1.setSoftwareInstalado("Office 2021, Chrome, VLC, Adobe Reader");
        ec1.setEstadoTeclado("Bueno");
        ec1.setEstadoMouse("Bueno");
        ec1.setEstadoCableEthernet("Bueno");
        ec1.setEstadoMonitor("Bueno");
        ec1.setProcesador("Intel Core i5-1135G7 @ 2.4GHz");
        ec1.setRam("16GB DDR4 3200MHz");
        ec1.setAlmacenamiento("512GB SSD NVMe");
        ec1.setContrasena("admin");
        ec1.setPasswordAdmin("Admin#2026");
        ec1.setLastPassword("Admin#2025");
        ec1.setEstadoCpu("Funciona");
        ec1.setEstadoComputadora("Funcionando");
        ec1.setObservaciones("Equipo asignado a la sala principal.");
        ec1.setUltimoUsuarioActualizacion(idAdmin);
        equipoComputoRepository.save(ec1);

        EquipoComputo ec2 = new EquipoComputo();
        ec2.setEquipo(equipos.get(1));
        ec2.setSala(cc01);
        ec2.setEspecificacionesHardware("Torre HP Pro 6300, 2 puertos USB 3.0 frontales, HDMI + VGA");
        ec2.setSistemaOperativo("Windows 10 Pro");
        ec2.setSoftwareInstalado("Office 2019, Firefox, AutoCAD 2023");
        ec2.setEstadoTeclado("Bueno");
        ec2.setEstadoMouse("Bueno");
        ec2.setEstadoCableEthernet("Bueno");
        ec2.setEstadoMonitor("Bueno");
        ec2.setProcesador("Intel Core i7-12700 @ 2.1GHz");
        ec2.setRam("32GB DDR4");
        ec2.setAlmacenamiento("1TB SSD");
        ec2.setContrasena("pc-cc01");
        ec2.setPasswordAdmin("PcAdmin2026");
        ec2.setEstadoCpu("Funciona");
        ec2.setEstadoComputadora("Funcionando");
        ec2.setUltimoUsuarioActualizacion(idOperador);
        equipoComputoRepository.save(ec2);

        EquipoComputo ec3 = new EquipoComputo();
        ec3.setEquipo(equipos.get(2));
        ec3.setSala(cc02);
        ec3.setEspecificacionesHardware("Torre Dell OptiPlex 3080, lector DVD, 6 USB");
        ec3.setSistemaOperativo("Ubuntu 22.04 LTS");
        ec3.setSoftwareInstalado("GNS3, Wireshark, VirtualBox");
        ec3.setEstadoTeclado("Teclas dañadas");
        ec3.setEstadoMouse("Bueno");
        ec3.setEstadoCableEthernet("Conector dañado");
        ec3.setEstadoMonitor("Bueno");
        ec3.setProcesador("Intel Core i5-10400 @ 2.9GHz");
        ec3.setRam("8GB DDR4");
        ec3.setAlmacenamiento("256GB SSD");
        ec3.setContrasena("ubuntu");
        ec3.setEstadoCpu("Con fallas");
        ec3.setEstadoComputadora("Requiere revisión");
        ec3.setObservaciones("Teclado con 3 teclas sin respuesta. Cable de red con conector flojo.");
        ec3.setUltimoUsuarioActualizacion(idOperador);
        equipoComputoRepository.save(ec3);

        EquipoComputo ec4 = new EquipoComputo();
        ec4.setEquipo(equipos.get(3));
        ec4.setSala(cc02);
        ec4.setEspecificacionesHardware("Torre Dell OptiPlex 3080, sin lector DVD");
        ec4.setSistemaOperativo("Windows 10 Pro");
        ec4.setSoftwareInstalado("Office 2019, Chrome");
        ec4.setEstadoTeclado("Bueno");
        ec4.setEstadoMouse("Scroll dañado");
        ec4.setEstadoCableEthernet("Bueno");
        ec4.setEstadoMonitor("Píxeles muertos");
        ec4.setProcesador("Intel Core i5-10400 @ 2.9GHz");
        ec4.setRam("8GB DDR4");
        ec4.setAlmacenamiento("256GB SSD");
        ec4.setContrasena("admin");
        ec4.setPasswordAdmin("Admin#2025");
        ec4.setEstadoCpu("Requiere mantenimiento");
        ec4.setEstadoComputadora("Requiere Mantenimiento");
        ec4.setObservaciones("Monitor con píxeles muertos en la esquina inferior derecha. Mouse con scroll defectuoso.");
        ec4.setUltimoUsuarioActualizacion(idAdmin);
        equipoComputoRepository.save(ec4);

        EquipoComputo ec5 = new EquipoComputo();
        ec5.setEquipo(equipos.get(4));
        ec5.setSala(cc03);
        ec5.setEspecificacionesHardware("Lenovo ThinkCentre M70q AIO, pantalla 23.8\", cámara web integrada");
        ec5.setSistemaOperativo("Windows 11 Pro");
        ec5.setSoftwareInstalado("Visual Studio 2022, IntelliJ IDEA, Office 2021, Git");
        ec5.setEstadoTeclado("Bueno");
        ec5.setEstadoMouse("Bueno");
        ec5.setEstadoCableEthernet("Bueno");
        ec5.setEstadoMonitor("Bueno");
        ec5.setProcesador("Intel Core i7-11700 @ 2.5GHz");
        ec5.setRam("16GB DDR4");
        ec5.setAlmacenamiento("512GB SSD");
        ec5.setContrasena("dev123");
        ec5.setPasswordAdmin("DevAdmin#2026");
        ec5.setLastPassword("DevAdmin#2025");
        ec5.setEstadoCpu("Funciona");
        ec5.setEstadoComputadora("Funcionando");
        ec5.setObservaciones("Equipo destinado a prácticas de programación.");
        ec5.setUltimoUsuarioActualizacion(idAdmin);
        equipoComputoRepository.save(ec5);
    }

    // ==========================================================
    // 9. RESERVAS Y SOLICITUDES
    // ==========================================================
    private void sembrarReservasYRoles(List<Sala> salas, List<Usuario> usuarios) {
        Usuario admin = usuarios.get(0);
        Usuario profesor = usuarios.get(2);
        Usuario profesor2 = usuarios.get(3);

        Sala cc01 = salas.get(0);
        Sala cc02 = salas.get(1);

        LocalDateTime hoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        UsoSala uso1 = new UsoSala();
        uso1.setSala(cc01);
        uso1.setUsuario(profesor);
        uso1.setFechaInicio(hoy.plusDays(1).withHour(9).withMinute(0));
        uso1.setFechaFin(hoy.plusDays(1).withHour(11).withMinute(0));
        uso1.setProposito("Clase de Programación Estructurada");
        uso1.setEstado("Confirmada");
        uso1.setCreadoPor(profesor);
        usoSalaRepository.save(uso1);

        UsoSala uso2 = new UsoSala();
        uso2.setSala(cc02);
        uso2.setUsuario(profesor2);
        uso2.setFechaInicio(hoy.plusDays(2).withHour(14).withMinute(0));
        uso2.setFechaFin(hoy.plusDays(2).withHour(16).withMinute(0));
        uso2.setProposito("Taller de Redes — Práctica con switches");
        uso2.setEstado("Confirmada");
        uso2.setCreadoPor(profesor2);
        usoSalaRepository.save(uso2);

        UsoSala uso3 = new UsoSala();
        uso3.setSala(cc01);
        uso3.setUsuario(profesor);
        uso3.setFechaInicio(hoy.plusDays(3).withHour(10).withMinute(0));
        uso3.setFechaFin(hoy.plusDays(3).withHour(12).withMinute(0));
        uso3.setProposito("Asesoría de proyectos");
        uso3.setEstado("Cancelada");
        uso3.setMotivoCancelacion("El docente solicitó cancelación por motivos personales.");
        uso3.setCreadoPor(profesor);
        uso3.setAdminModifico(admin);
        usoSalaRepository.save(uso3);

        SolicitudReserva sol1 = new SolicitudReserva();
        sol1.setSolicitante(profesor2);
        sol1.setSala(cc01);
        sol1.setFechaInicio(hoy.plusDays(4).withHour(8).withMinute(0));
        sol1.setFechaFin(hoy.plusDays(4).withHour(10).withMinute(0));
        sol1.setJustificacion("Se requiere la sala para examen extraordinario de bases de datos.");
        sol1.setEstado("Pendiente");
        solicitudReservaRepository.save(sol1);

        UsoSala usoAprobado = new UsoSala();
        usoAprobado.setSala(cc02);
        usoAprobado.setUsuario(profesor);
        usoAprobado.setFechaInicio(hoy.plusDays(5).withHour(15).withMinute(0));
        usoAprobado.setFechaFin(hoy.plusDays(5).withHour(17).withMinute(0));
        usoAprobado.setProposito("Práctica de laboratorio de redes");
        usoAprobado.setEstado("Confirmada");
        usoAprobado.setCreadoPor(admin);
        usoAprobado.setAdminModifico(admin);
        usoSalaRepository.save(usoAprobado);

        SolicitudReserva sol2 = new SolicitudReserva();
        sol2.setSolicitante(profesor);
        sol2.setSala(cc02);
        sol2.setFechaInicio(hoy.plusDays(5).withHour(15).withMinute(0));
        sol2.setFechaFin(hoy.plusDays(5).withHour(17).withMinute(0));
        sol2.setJustificacion("Laboratorio de redes para práctica grupal.");
        sol2.setEstado("Aprobada");
        sol2.setAdminAtendio(admin);
        sol2.setUsoGenerado(usoAprobado);
        solicitudReservaRepository.save(sol2);

        SolicitudReserva sol3 = new SolicitudReserva();
        sol3.setSolicitante(profesor2);
        sol3.setSala(cc02);
        sol3.setFechaInicio(hoy.plusDays(6).withHour(9).withMinute(0));
        sol3.setFechaFin(hoy.plusDays(6).withHour(11).withMinute(0));
        sol3.setJustificacion("Se solicita la sala para capacitación externa.");
        sol3.setEstado("Rechazada");
        sol3.setAdminAtendio(admin);
        sol3.setMotivoRechazo("La sala ya cuenta con reserva confirmada en ese horario.");
        solicitudReservaRepository.save(sol3);
    }

    // ==========================================================
    // 10. SOLICITUDES TÉCNICAS (Mesa de Ayuda)
    // ==========================================================
    private void sembrarSolicitudesTecnicas(List<Usuario> usuarios) {
        // 1. Catálogo de Tipos
        CatalogoTipoSolicitud tipoPassword = tipoSolicitudRepository.save(
                crearTipo("REINICIO_PASSWORD", "Restablecimiento de contraseña de correo institucional"));
        CatalogoTipoSolicitud tipoAuth = tipoSolicitudRepository.save(
                crearTipo("REINICIO_AUTHENTICATOR", "Restablecimiento de método de autenticación (MFA)"));
        tipoSolicitudRepository.save(
                crearTipo("OTRO", "Otros problemas que requieran atención de TICS"));

        Usuario alumno = usuarios.get(2); // Usaremos al profesor como si fuera alumno para la prueba
        Usuario admin = usuarios.get(0);

        // 2. Solicitud de prueba en estado APROBADA (para que el alumno llene el formulario)
        SolicitudTecnica solAprobada = new SolicitudTecnica();
        solAprobada.setUsuario(alumno);
        solAprobada.setTipo(tipoAuth);
        solAprobada.setMotivo("Cambié de teléfono y perdí el acceso a mi correo institucional.");
        solAprobada.setEstado(EstadoSolicitud.APROBADA);
        solAprobada.setAdminRevisor(admin);
        solAprobada.setFechaRevision(LocalDateTime.now());
        solicitudTecnicaRepository.save(solAprobada);

        registrarHistorial(solAprobada, alumno, null, EstadoSolicitud.PENDIENTE, "Solicitud creada por el alumno.");
        registrarHistorial(solAprobada, admin, EstadoSolicitud.PENDIENTE, EstadoSolicitud.APROBADA, "Solicitud aprobada.");

        // 3. Solicitud de prueba en estado TICKET_CREADO (para que el admin la resuelva)
        SolicitudTecnica solTicketCreado = new SolicitudTecnica();
        solTicketCreado.setUsuario(alumno);
        solTicketCreado.setTipo(tipoPassword);
        solTicketCreado.setMotivo("Olvidé mi contraseña de acceso al SIA.");
        solTicketCreado.setEstado(EstadoSolicitud.TICKET_CREADO);
        solTicketCreado.setAdminRevisor(admin);
        solTicketCreado.setFechaRevision(LocalDateTime.now());
        solicitudTecnicaRepository.save(solTicketCreado);

        registrarHistorial(solTicketCreado, alumno, null, EstadoSolicitud.PENDIENTE, "Solicitud creada.");
        registrarHistorial(solTicketCreado, admin, EstadoSolicitud.PENDIENTE, EstadoSolicitud.APROBADA, "Aprobada.");

        // Simular formulario enviado
        FormularioDetalle form = new FormularioDetalle();
        form.setSolicitud(solTicketCreado);
        form.setNombreCompleto("Roberto Gómez");
        form.setCorreoInstitucional("profesor@uaemex.mx");
        form.setCorreoAlterno("roberto@gmail.com");
        form.setNumeroCuenta("1234567");
        form.setCurpHash("HASH_DE_PRUEBA_NO_REAL");
        form.setTelefono("7221234567");
        form.setDescripcionProblema("No puedo acceder al sistema desde que cambié mi contraseña.");
        formularioDetalleRepository.save(form);

        registrarHistorial(solTicketCreado, alumno, EstadoSolicitud.APROBADA, EstadoSolicitud.FORMULARIO_ENVIADO, "Formulario enviado.");
        registrarHistorial(solTicketCreado, admin, EstadoSolicitud.FORMULARIO_ENVIADO, EstadoSolicitud.TICKET_CREADO, "Ticket generado en plataforma externa.");
    }

    private CatalogoTipoSolicitud crearTipo(String nombre, String descripcion) {
        CatalogoTipoSolicitud tipo = new CatalogoTipoSolicitud();
        tipo.setNombre(nombre);
        tipo.setDescripcion(descripcion);
        tipo.setActivo(true);
        return tipo;
    }

    private void registrarHistorial(SolicitudTecnica solicitud, Usuario usuario, EstadoSolicitud anterior, EstadoSolicitud nuevo, String comentario) {
        HistorialSolicitud hist = new HistorialSolicitud();
        hist.setSolicitud(solicitud);
        hist.setUsuarioAccion(usuario);
        hist.setEstadoAnterior(anterior);
        hist.setEstadoNuevo(nuevo);
        hist.setComentario(comentario);
        historialSolicitudRepository.save(hist);
    }


}