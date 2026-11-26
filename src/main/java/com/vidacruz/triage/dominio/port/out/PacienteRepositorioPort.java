package com.vidacruz.triage.dominio.port.out;

import com.vidacruz.triage.dominio.entidades.Paciente;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PacienteRepositorioPort {
    Mono<Paciente> guardar(Paciente paciente);
    Flux<Paciente> buscarPacientesConSlaVencido();
    Mono<Integer> marcarComoEscaladoSiAplica(Long pacienteId);
}
