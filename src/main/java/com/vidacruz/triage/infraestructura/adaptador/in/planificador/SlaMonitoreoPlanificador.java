package com.vidacruz.triage.infraestructura.adaptador.in.planificador;

import com.vidacruz.triage.dominio.port.out.AuditoriaTriageRepositorioPort;
import org.springframework.transaction.reactive.TransactionalOperator;
import com.vidacruz.triage.dominio.port.out.PacienteRepositorioPort;
import com.vidacruz.triage.infraestructura.config.TriageProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import com.vidacruz.triage.dominio.port.out.GestorMensajePort;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.vidacruz.triage.dominio.entidades.*;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlaMonitoreoPlanificador {
    private static final String EVENTO_ESCALADO = "paciente_escalado";

    private final PacienteRepositorioPort pacienteRepositorioPort;
    private final AuditoriaTriageRepositorioPort auditoriaTriageRepositorioPort;
    private final GestorMensajePort gestorMensajePort;
    private final TransactionalOperator transactionalOperator;
    private final TriageProperties triageProperties;
    private Disposable schedulerSubscription;

    @EventListener(ApplicationReadyEvent.class)
    public void iniciarMonitoreoSla() {
        Duration intervalo = Duration.ofMillis(triageProperties.getScheduler().getIntervaloEvaluacionMs());
        log.info("Iniciando scheduler reactivo de SLA cada {} ms", intervalo.toMillis());

        this.schedulerSubscription = Flux.interval(intervalo)
                .concatMap(tick -> evaluarPacientesConSlaVencido()
                        .onErrorResume(ex -> {
                            log.error("Error en ciclo de evaluación de SLA (tick {}): {}", tick, ex.getMessage(), ex);
                            return Mono.empty();
                        }))
                .subscribe();
    }

    private Mono<Void> evaluarPacientesConSlaVencido() {
        return pacienteRepositorioPort.buscarPacientesConSlaVencido()
                .flatMap(this::procesarEscalamientoIdempotente)
                .then();
    }

    private Mono<Void> procesarEscalamientoIdempotente(Paciente paciente) {
        Instant ahora = Instant.now();

        // 1. Operación atómica en BD (RN-03 y RN-04)
        Mono<Boolean> escalarYAuditar = pacienteRepositorioPort.marcarComoEscaladoSiAplica(paciente.getId())
                .filter(filasActualizadas -> filasActualizadas == 1)
                .flatMap(filas -> {
                    AuditoriaTriage auditoria = AuditoriaTriage.builder()
                            .pacienteId(paciente.getId())
                            .tipoEvento(TipoEventoAuditoria.ESCALAMIENTO.name())
                            .estadoAnterior(EstadoPaciente.REGISTRADO)
                            .estadoNuevo(EstadoPaciente.REGISTRADO)
                            .detalle("SLA de " + paciente.getSlaMinutos() + " min superado")
                            .fechaEvento(ahora)
                            .build();
                    return auditoriaTriageRepositorioPort.guardar(auditoria).thenReturn(true);
                })
                .defaultIfEmpty(false)
                .as(transactionalOperator::transactional);

        // 2. Solo si la BD confirmó el escalamiento en este intento, publicar en Kafka
        return escalarYAuditar
                .filter(Boolean::booleanValue)
                .flatMap(escalado -> publicarEventoEscalado(paciente, ahora))
                .onErrorResume(ex -> {
                    log.error("Fallo al escalar paciente {}: {}", paciente.getId(), ex.getMessage(), ex);
                    return Mono.empty();
                });
    }

    private Mono<Void> publicarEventoEscalado(Paciente paciente, Instant fechaEvento) {
        TriageEvent evento = new TriageEvent(
                EVENTO_ESCALADO,
                paciente.getId(),
                paciente.getNivelTriage(),
                EstadoPaciente.REGISTRADO,
                paciente.getSlaMinutos(),
                fechaEvento
        );

        return gestorMensajePort.publicarEvento(evento).then();
    }

    @PreDestroy
    public void detenerMonitoreoSla() {
        if (schedulerSubscription != null && !schedulerSubscription.isDisposed()) {
            log.info("Deteniendo scheduler reactivo de SLA...");
            schedulerSubscription.dispose();
        }
    }
}
