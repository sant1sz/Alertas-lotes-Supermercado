package com.example.alertalote.repository;

import com.example.alertalote.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    /** Lotes con unidades que vencen entre hoy y la fecha límite (incluidas), ordenados por fecha. */
    @Query("""
            SELECT l FROM Lote l
            WHERE l.cantidad > 0
              AND l.fechaVencimiento BETWEEN :hoy AND :fechaLimite
            ORDER BY l.fechaVencimiento ASC
            """)
    List<Lote> findProximosAVencer(@Param("hoy") LocalDate hoy,
                                   @Param("fechaLimite") LocalDate fechaLimite);

    /** Lotes con unidades cuya fecha de vencimiento ya pasó. */
    @Query("""
            SELECT l FROM Lote l
            WHERE l.cantidad > 0
              AND l.fechaVencimiento < :hoy
            ORDER BY l.fechaVencimiento ASC
            """)
    List<Lote> findVencidos(@Param("hoy") LocalDate hoy);

    /** Todos los lotes con unidades que vencen hasta la fecha límite (vencidos y próximos). */
    @Query("""
            SELECT l FROM Lote l
            WHERE l.cantidad > 0
              AND l.fechaVencimiento <= :fechaLimite
            """)
    List<Lote> findEnRiesgo(@Param("fechaLimite") LocalDate fechaLimite);

    List<Lote> findByProductoNegocioIdNegocio(Long idNegocio);

    List<Lote> findByProductoCodigoBarras(String codigoBarras);

    boolean existsByProductoCodigoBarras(String codigoBarras);
}
