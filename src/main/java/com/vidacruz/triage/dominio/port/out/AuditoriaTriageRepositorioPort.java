package com.vidacruz.triage.dominio.port.out;

import com.vidacruz.triage.dominio.entidades.AuditoriaTriage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface AuditoriaTriageRepositorioPort {
    Mono<AuditoriaTriage> guardar(AuditoriaTriage auditoria);
    Flux<AuditoriaTriage> buscarPorIdPacienteOrdenadoPorFechaEventoAscendente(Long pacienteId);
}
