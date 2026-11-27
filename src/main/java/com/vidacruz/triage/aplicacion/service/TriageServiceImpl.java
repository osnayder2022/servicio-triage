package com.vidacruz.triage.aplicacion.service;

import com.vidacruz.triage.aplicacion.casouso.port.in.RegistrarPacienteCasoUso;
import com.vidacruz.triage.dominio.port.out.AuditoriaTriageRepositorioPort;
import com.vidacruz.triage.dominio.entidades.RegistroPacienteResponse;
import org.springframework.transaction.reactive.TransactionalOperator;
import com.vidacruz.triage.dominio.port.out.PacienteRepositorioPort;
import com.vidacruz.triage.dominio.port.out.GestorMensajePort;
import com.vidacruz.triage.dominio.port.out.SlaTriagePort;
import com.vidacruz.triage.dominio.entidades.*;
import reactor.core.publisher.Mono;
import java.time.Instant;


public class TriageServiceImpl implements RegistrarPacienteCasoUso {

    private final AuditoriaTriageRepositorioPort auditoriaTriageRepositorioPort;
    private final PacienteRepositorioPort pacienteRepositorioPort;
    private final TransactionalOperator transactionalOperator;
    private final GestorMensajePort gestorMensajePort;
    private final SlaTriagePort slaTriagePort;

    public TriageServiceImpl(AuditoriaTriageRepositorioPort auditoriaTriageRepositorioPort,
                             PacienteRepositorioPort pacienteRepositorioPort,
                             TransactionalOperator transactionalOperator,
                             GestorMensajePort gestorMensajePort,
                             SlaTriagePort slaTriagePort) {
        this.auditoriaTriageRepositorioPort = auditoriaTriageRepositorioPort;
        this.pacienteRepositorioPort = pacienteRepositorioPort;
        this.transactionalOperator = transactionalOperator;
        this.gestorMensajePort = gestorMensajePort;
        this.slaTriagePort = slaTriagePort;
    }

    @Override
    public Mono<RegistroPacienteResponse> registrar(Paciente paciente) {
        Instant ahora = Instant.now();

        paciente.setEstado(EstadoPaciente.REGISTRADO);
        paciente.setEscalado(false);
        paciente.setFechaRegistro(ahora);
        paciente.setFechaUltimoCambioEstado(ahora);

        NivelTriage nivel = paciente.clasificar();
        Integer slaMinutos = slaTriagePort.obtenerSla(nivel);

        paciente.setNivelTriage(nivel);
        paciente.setSlaMinutos(slaMinutos);


        Mono<Paciente> guardarYAuditar = pacienteRepositorioPort.guardar(paciente)
                .flatMap(guardado -> {
                    AuditoriaTriage auditoria = AuditoriaTriage.builder()
                            .pacienteId(guardado.getId())
                            .tipoEvento(TipoEventoAuditoria.REGISTRO.name())
                            .estadoAnterior(null)
                            .estadoNuevo(EstadoPaciente.REGISTRADO)
                            .detalle("Clasificado como " + nivel + " (SLA: " + slaMinutos + " min)")
                            .fechaEvento(ahora)
                            .build();

                    return auditoriaTriageRepositorioPort.guardar(auditoria).thenReturn(guardado);
                })
                .as(transactionalOperator::transactional);

        return guardarYAuditar
        .flatMap(guardado -> {
            TriageEvent evento = new TriageEvent(
                    "paciente_registrado",
                    guardado.getId(),
                    guardado.getNivelTriage(),
                    guardado.getEstado(),
                    guardado.getSlaMinutos(),
                    ahora
            );

            return gestorMensajePort.publicarEvento(evento)
                    .thenReturn(new RegistroPacienteResponse(
                            guardado.getId(),
                            guardado.getNivelTriage(),
                            guardado.getSlaMinutos(),
                            guardado.getEstado(),
                            guardado.getFechaRegistro()
                    ));
        });
    }

}
