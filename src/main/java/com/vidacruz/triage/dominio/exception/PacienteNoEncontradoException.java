package com.vidacruz.triage.dominio.exception;

public class PacienteNoEncontradoException extends RuntimeException {
    public PacienteNoEncontradoException(Long id) {
        super("Paciente no encontrado con id: " + id);
    }
}
