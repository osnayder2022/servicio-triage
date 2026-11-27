package com.vidacruz.triage.aplicacion.service;

import com.vidacruz.triage.infraestructura.adaptador.in.web.exception.TransicionEstadoInvalidaException;
import com.vidacruz.triage.infraestructura.adaptador.in.web.exception.PacienteNoEncontradoException;
import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.PacienteDetalleResponse;
import com.vidacruz.triage.aplicacion.casouso.port.in.GestionarEstadoPacienteCasoUso;
import com.vidacruz.triage.dominio.port.out.AuditoriaTriageRepositorioPort;
import org.springframework.transaction.reactive.TransactionalOperator;
import com.vidacruz.triage.dominio.port.out.PacienteRepositorioPort;
import com.vidacruz.triage.dominio.port.out.GestorMensajePort;
import com.vidacruz.triage.dominio.entidades.*;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;
import java.time.Instant;

public class GestionarEstadoPacienteServiceImpl implements GestionarEstadoPacienteCasoUso {

    private final AuditoriaTriageRepositorioPort auditoriaTriageRepositorioPort;
    private final PacienteRepositorioPort pacienteRepositorioPort;
    private final TransactionalOperator transactionalOperator;
    private final GestorMensajePort gestorMensajePort;
    private final ModelMapper mapper ;

    public GestionarEstadoPacienteServiceImpl(AuditoriaTriageRepositorioPort auditoriaTriageRepositorioPort,
                                              PacienteRepositorioPort pacienteRepositorioPort,
                                              TransactionalOperator transactionalOperator,
                                              GestorMensajePort gestorMensajePort,
                                              ModelMapper mapper){
        this.auditoriaTriageRepositorioPort = auditoriaTriageRepositorioPort;
        this.pacienteRepositorioPort = pacienteRepositorioPort;
        this.transactionalOperator = transactionalOperator;
        this.gestorMensajePort = gestorMensajePort;
        this.mapper = mapper;
    }

    @Override
    public Mono<PacienteDetalleResponse> cambiarEstado(Long pacienteId, EstadoPaciente nuevoEstado) {
        Instant ahora = Instant.now();

        Mono<Paciente> actualizarYAuditar = pacienteRepositorioPort.buscarPorId(pacienteId)
                .switchIfEmpty(Mono.error(new PacienteNoEncontradoException(pacienteId)))
                .flatMap(paciente -> {

                    if (!paciente.esTransicionValida(nuevoEstado)) {
                        return Mono.error(new TransicionEstadoInvalidaException(
                                String.format("Transición no permitida de %s a %s", paciente.getEstado(), nuevoEstado)
                        ));
                    }

                    EstadoPaciente estadoAnterior = paciente.getEstado();
                    paciente.setEstado(nuevoEstado);
                    paciente.setFechaUltimoCambioEstado(ahora);

                    TipoEventoAuditoria tipoAuditoria = (nuevoEstado == EstadoPaciente.EN_CONSULTA)
                            ? TipoEventoAuditoria.ENTRADA_A_CONSULTA
                            : TipoEventoAuditoria.ALTA;

                    return pacienteRepositorioPort.guardar(paciente)
                            .flatMap(actualizado -> {
                                AuditoriaTriage auditoria = AuditoriaTriage.builder()
                                        .pacienteId(actualizado.getId())
                                        .tipoEvento(tipoAuditoria.name())
                                        .estadoAnterior(estadoAnterior)
                                        .estadoNuevo(nuevoEstado)
                                        .detalle("Cambio de estado clínico")
                                        .fechaEvento(ahora)
                                        .build();

                                return auditoriaTriageRepositorioPort.guardar(auditoria).thenReturn(actualizado);
                            });
                })
                .as(transactionalOperator::transactional);

        return actualizarYAuditar
                .flatMap(actualizado -> {
                    String nombreEventoKafka = (nuevoEstado == EstadoPaciente.EN_CONSULTA)
                            ? "paciente_en_consulta"
                            : "paciente_alta";

                    TriageEvent evento = new TriageEvent(
                            nombreEventoKafka,
                            actualizado.getId(),
                            actualizado.getNivelTriage(),
                            actualizado.getEstado(),
                            actualizado.getSlaMinutos(),
                            ahora
                    );


                    return gestorMensajePort.publicarEvento(evento)
                            .thenReturn(mapper.map(actualizado,PacienteDetalleResponse.class));
                });
    }

    @Override
    public Mono<PacienteDetalleResponse> obtenerPorId(Long pacienteId) {
        return pacienteRepositorioPort.buscarPorId(pacienteId)
                .switchIfEmpty(Mono.error(new PacienteNoEncontradoException(pacienteId)))
                .map(paciente -> mapper.map(paciente,PacienteDetalleResponse.class));
    }


}
