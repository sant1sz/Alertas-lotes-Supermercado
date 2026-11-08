package com.example.alertalote.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
        @NotBlank(message = "El código de barras es obligatorio")
        @Size(max = 50, message = "El código de barras no puede tener más de 50 caracteres")
        String codigoBarras,

        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombre,

        String categoria,

        @NotNull(message = "El id del negocio es obligatorio")
        Long idNegocio
) { }
