package com.example.alertalote.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Unidades que salen de un lote (venta, merma o retiro por vencimiento). */
public record MovimientoRequest(
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que 0")
        Integer cantidad
) { }
