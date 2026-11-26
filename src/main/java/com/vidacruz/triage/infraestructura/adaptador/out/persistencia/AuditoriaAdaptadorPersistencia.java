package com.vidacruz.triage.infraestructura.adaptador.out.persistencia;

import com.vidacruz.triage.infraestructura.adaptador.out.persistencia.repositorios.AuditoriaTriageRepositorio;
import com.vidacruz.triage.infraestructura.adaptador.out.persistencia.entidades.AuditoriaTriageEntidad;
import com.vidacruz.triage.dominio.port.out.AuditoriaTriageRepositorioPort;
import com.vidacruz.triage.dominio.entidades.AuditoriaTriage;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.modelmapper.ModelMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditoriaAdaptadorPersistencia implements AuditoriaTriageRepositorioPort {

    private final AuditoriaTriageRepositorio auditoriaTriageRepositorio;
    private final ModelMapper mapper;

    @Override
    public Mono<AuditoriaTriage> guardar(AuditoriaTriage auditoria) {
        return Mono.fromCallable(() -> mapper.map(auditoria, AuditoriaTriageEntidad.class))
                .flatMap(auditoriaTriageRepositorio::save)
                .map(entidad -> mapper.map(entidad, AuditoriaTriage.class))
                .doOnSuccess(auditoriaGuardada -> log.info("Auditoria guardada exitosamente"));
    }

    @Override
    public Flux<AuditoriaTriage> buscarPorIdPacienteOrdenadoPorFechaEventoAscendente(Long pacienteId) {
        return auditoriaTriageRepositorio.findByPacienteIdOrderByFechaEventoAsc(pacienteId)
                .map(entidad -> mapper.map(entidad, AuditoriaTriage.class));
    }
}