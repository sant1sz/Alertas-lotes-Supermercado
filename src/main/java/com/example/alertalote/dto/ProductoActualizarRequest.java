package com.example.alertalote.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductoActualizarRequest(
        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombre,

        String categoria,

        @NotNull(message = "El id del negocio es obligatorio")
        Long idNegocio
) { }
