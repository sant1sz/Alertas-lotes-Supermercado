package com.example.alertalote.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NegocioRequest(
        @NotBlank(message = "El nombre de la tienda es obligatorio")
        @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
        String nombreTienda,

        @NotBlank(message = "El teléfono de WhatsApp es obligatorio")
        @Pattern(regexp = "^\\+?[0-9 ]{7,20}$", message = "El teléfono solo puede tener números (y opcionalmente + al inicio)")
        String telefonoWhatsapp
) { }
