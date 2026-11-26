package com.vidacruz.triage.infraestructura.adaptador.out.persistencia.repositorios;

import com.vidacruz.triage.infraestructura.adaptador.out.persistencia.entidades.AuditoriaTriageEntidad;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuditoriaTriageRepositorio extends Repository<AuditoriaTriageEntidad, Long> {
    Mono<AuditoriaTriageEntidad> save(AuditoriaTriageEntidad entidad);
    Flux<AuditoriaTriageEntidad> findByPacienteIdOrderByFechaEventoAsc(Long pacienteId);
}
