package com.vidacruz.triage.infraestructura.adaptador.in.web.exception;

public class PacienteNoEncontradoException extends RuntimeException {
    public PacienteNoEncontradoException(Long id) {
        super("Paciente no encontrado con id: " + id);
    }
}
