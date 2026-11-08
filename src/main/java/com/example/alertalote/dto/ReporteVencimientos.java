package com.example.alertalote.dto;

import com.example.alertalote.model.Lote;

import java.time.LocalDate;
import java.util.List;

/** Resumen de un negocio: lotes vencidos y próximos a vencer, del más urgente al menos urgente. */
public record ReporteVencimientos(
        Long idNegocio,
        String nombreTienda,
        LocalDate fechaReporte,
        int diasAnticipacion,
        List<Lote> vencidos,
        List<Lote> proximosAVencer,
        int unidadesVencidas,
        int unidadesProximasAVencer
) { }
