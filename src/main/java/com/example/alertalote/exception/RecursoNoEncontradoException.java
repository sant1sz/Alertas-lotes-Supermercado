package com.example.alertalote.exception;

/** Se lanza cuando se busca algo que no existe. Responde 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
}
