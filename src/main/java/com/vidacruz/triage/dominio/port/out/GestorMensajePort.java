package com.vidacruz.triage.dominio.port.out;

import com.vidacruz.triage.dominio.entidades.Paciente;
import com.vidacruz.triage.dominio.entidades.TriageEvent;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface GestorMensajePort {
    Mono<Void> publicarEvento(TriageEvent evento);
}
