package com.example.alertalote.dto;

/** Mensaje listo para enviar: abrir "enlace" abre WhatsApp con el texto escrito. */
public record MensajeWhatsapp(
        String telefono,
        int alertasIncluidas,
        String mensaje,
        String enlace
) { }
