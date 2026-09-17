package com.tics.tics.modelos;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipos_computo")
@Data
public class EquipoComputo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo_computo")
    private Integer idEquipoComputo;

    // Relación 1:1 con la tabla Equipos
    @OneToOne
    @JoinColumn(name = "inventario", referencedColumnName = "inventario", nullable = false, unique = true)
    private Equipo equipo;

    @Column(name = "especificaciones_hardware", columnDefinition = "TEXT")
    private String especificacionesHardware;

    @Column(name = "sistema_operativo")
    private String sistemaOperativo;

    @Column(name = "software_instalado", columnDefinition = "TEXT")
    private String softwareInstalado;

    @Column(name = "estado_teclado")
    private String estadoTeclado;

    @Column(name = "estado_mouse")
    private String estadoMouse;

    @Column(name = "estado_cable_ethernet")
    private String estadoCableEthernet;

    @Column(name = "estado_monitor")
    private String estadoMonitor;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
