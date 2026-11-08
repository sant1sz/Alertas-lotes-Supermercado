package com.example.alertalote.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record LoteRequest(
        @NotBlank(message = "El código de barras del producto es obligatorio")
        String codigoBarras,

        @NotNull(message = "La cantidad es obligatoria")
        @PositiveOrZero(message = "La cantidad no puede ser negativa")
        Integer cantidad,

        @NotNull(message = "La fecha de vencimiento es obligatoria (formato AAAA-MM-DD)")
        LocalDate fechaVencimiento,

        /** Opcional: si no se envía, queda "Estantería". */
        String ubicacion
) { }
