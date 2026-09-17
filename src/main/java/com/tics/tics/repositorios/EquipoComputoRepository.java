package com.tics.tics.repositorios;

import com.tics.tics.modelos.EquipoComputo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EquipoComputoRepository extends JpaRepository<EquipoComputo, Integer> {
    Optional<EquipoComputo> findByEquipo_Inventario(String inventario);
}