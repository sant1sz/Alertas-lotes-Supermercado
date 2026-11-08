package com.example.alertalote.repository;

import com.example.alertalote.model.Alerta;
import com.example.alertalote.model.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByAtendidaFalseOrderByFechaVencimientoAsc();

    List<Alerta> findByLoteProductoNegocioIdNegocioAndAtendidaFalseOrderByFechaVencimientoAsc(Long idNegocio);

    List<Alerta> findByLoteIdLoteAndTipoAndAtendidaFalse(Long idLote, TipoAlerta tipo);

    boolean existsByLoteIdLoteAndTipoAndAtendidaFalse(Long idLote, TipoAlerta tipo);

    @Modifying
    @Query("DELETE FROM Alerta a WHERE a.lote.idLote = :idLote")
    void deleteByLoteId(@Param("idLote") Long idLote);
}
