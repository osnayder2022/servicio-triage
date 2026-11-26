package com.vidacruz.triage.dominio.port.out;

import com.vidacruz.triage.dominio.entidades.Paciente;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface GestorMensajePort {
    Mono<Void> publicarEvento(String tipoEvento, Paciente paciente, Instant fechaEvento);
}
