package com.example.alertalote.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorRespuesta(
        LocalDateTime fecha,
        int estado,
        String error,
        String mensaje,
        Map<String, String> detalles
) { }
