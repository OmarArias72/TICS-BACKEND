package com.tics.tics.inventario.entity;

import com.tics.tics.salas.entity.Sala;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipos_computo")
public class EquipoComputo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo_computo")
    private Integer idEquipoComputo;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "inventario", referencedColumnName = "inventario", nullable = false, unique = true)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sala")
    private Sala sala;

    @Column(name = "especificaciones_hardware", columnDefinition = "TEXT")
    private String especificacionesHardware;

    @Column(name = "sistema_operativo", length = 100)
    private String sistemaOperativo;

    @Column(name = "software_instalado", columnDefinition = "TEXT")
    private String softwareInstalado;

    // ===== Periféricos (con CHECKs en BD) =====
    // Valores: 'Bueno', 'Teclas dañadas', 'Teclas faltantes', 'Sin respuesta', 'Cable dañado', 'Dañado'
    @Column(name = "estado_teclado", length = 50)
    private String estadoTeclado;

    // Valores: 'Bueno', 'Clic dañado', 'Scroll dañado', 'Sin respuesta', 'Cable dañado', 'Dañado'
    @Column(name = "estado_mouse", length = 50)
    private String estadoMouse;

    // Valores: 'Bueno', 'Conector dañado', 'Cable cortado', 'Sin conexión', 'Dañado'
    @Column(name = "estado_cable_ethernet", length = 50)
    private String estadoCableEthernet;

    // Valores: 'Bueno', 'Sin imagen', 'Imagen distorsionada', 'Píxeles muertos', 'Pantalla rayada', 'Pantalla rota', 'Dañado'
    @Column(name = "estado_monitor", length = 50)
    private String estadoMonitor;

    @Column(name = "procesador", length = 100)
    private String procesador;

    @Column(name = "ram", length = 50)
    private String ram;

    @Column(name = "almacenamiento", length = 100)
    private String almacenamiento;

    // ===== Contraseñas =====
    @Column(name = "contrasena", columnDefinition = "TEXT")
    private String contrasena;

    @Column(name = "password_admin", columnDefinition = "TEXT")
    private String passwordAdmin;

    @Column(name = "last_password", columnDefinition = "TEXT")
    private String lastPassword;

    // ===== Estados =====
    // estado_cpu: 'Funciona', 'No funciona', 'Con fallas', 'En revisión', 'Requiere mantenimiento'
    @Column(name = "estado_cpu", length = 50)
    private String estadoCpu;

    // estado_computadora: 'Funcionando', 'No funciona', 'Requiere revisión', 'Requiere Mantenimiento'
    @Column(name = "estado_computadora", length = 50)
    private String estadoComputadora;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "ultimo_usuario_actualizacion")
    private Integer ultimoUsuarioActualizacion;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    public EquipoComputo() {}

    // ===== Getters / Setters =====

    public Integer getIdEquipoComputo() { return idEquipoComputo; }
    public void setIdEquipoComputo(Integer idEquipoComputo) { this.idEquipoComputo = idEquipoComputo; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public Sala getSala() { return sala; }
    public void setSala(Sala sala) { this.sala = sala; }

    public String getEspecificacionesHardware() { return especificacionesHardware; }
    public void setEspecificacionesHardware(String especificacionesHardware) { this.especificacionesHardware = especificacionesHardware; }

    public String getSistemaOperativo() { return sistemaOperativo; }
    public void setSistemaOperativo(String sistemaOperativo) { this.sistemaOperativo = sistemaOperativo; }

    public String getSoftwareInstalado() { return softwareInstalado; }
    public void setSoftwareInstalado(String softwareInstalado) { this.softwareInstalado = softwareInstalado; }

    public String getEstadoTeclado() { return estadoTeclado; }
    public void setEstadoTeclado(String estadoTeclado) { this.estadoTeclado = estadoTeclado; }

    public String getEstadoMouse() { return estadoMouse; }
    public void setEstadoMouse(String estadoMouse) { this.estadoMouse = estadoMouse; }

    public String getEstadoCableEthernet() { return estadoCableEthernet; }
    public void setEstadoCableEthernet(String estadoCableEthernet) { this.estadoCableEthernet = estadoCableEthernet; }

    public String getEstadoMonitor() { return estadoMonitor; }
    public void setEstadoMonitor(String estadoMonitor) { this.estadoMonitor = estadoMonitor; }

    public String getProcesador() { return procesador; }
    public void setProcesador(String procesador) { this.procesador = procesador; }

    public String getRam() { return ram; }
    public void setRam(String ram) { this.ram = ram; }

    public String getAlmacenamiento() { return almacenamiento; }
    public void setAlmacenamiento(String almacenamiento) { this.almacenamiento = almacenamiento; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getPasswordAdmin() { return passwordAdmin; }
    public void setPasswordAdmin(String passwordAdmin) { this.passwordAdmin = passwordAdmin; }

    public String getLastPassword() { return lastPassword; }
    public void setLastPassword(String lastPassword) { this.lastPassword = lastPassword; }

    public String getEstadoCpu() { return estadoCpu; }
    public void setEstadoCpu(String estadoCpu) { this.estadoCpu = estadoCpu; }

    public String getEstadoComputadora() { return estadoComputadora; }
    public void setEstadoComputadora(String estadoComputadora) { this.estadoComputadora = estadoComputadora; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getUltimoUsuarioActualizacion() { return ultimoUsuarioActualizacion; }
    public void setUltimoUsuarioActualizacion(Integer ultimoUsuarioActualizacion) { this.ultimoUsuarioActualizacion = ultimoUsuarioActualizacion; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
}