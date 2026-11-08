package com.example.alertalote.exception;

/** Se lanza cuando la petición no tiene sentido para el negocio. Responde 400. */
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) { super(mensaje); }
}
