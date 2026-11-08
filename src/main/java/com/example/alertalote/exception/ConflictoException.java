package com.example.alertalote.exception;

/** Se lanza cuando la operación choca con datos existentes. Responde 409. */
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) { super(mensaje); }
}
