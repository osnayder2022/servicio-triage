package com.vidacruz.triage.infraestructura.adaptador.out.persistencia;

import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import com.vidacruz.triage.dominio.port.out.GestorMensajePort;
import com.vidacruz.triage.dominio.entidades.TriageEvent;
import com.vidacruz.triage.dominio.entidades.Paciente;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class GestorMensajeAdaptadorPersistencia implements GestorMensajePort {

    private static final String TOPIC_TRIAJE_EVENTOS = "triaje.eventos";
    private final ReactiveKafkaProducerTemplate<String, TriageEvent> kafkaTemplate;

    @Override
    public Mono<Void> publicarEvento(TriageEvent evento) {

        return kafkaTemplate.send(TOPIC_TRIAJE_EVENTOS, String.valueOf(evento.pacienteId()), evento)
                .doOnSuccess(res -> log.info("Evento '{}' publicado para paciente {}", evento.tipoEvento(), evento.pacienteId()))
                .then();
    }
}
