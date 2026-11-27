package com.vidacruz.triage.aplicacion.casouso.service;

import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.RegistroPacienteResponse;
import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.RegistroPacienteRequest;
import com.vidacruz.triage.aplicacion.casouso.port.in.RegistrarPacienteCasoUso;
import com.vidacruz.triage.dominio.port.out.AuditoriaTriageRepositorioPort;
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
    public Mono<RegistroPacienteResponse> registrar(RegistroPacienteRequest request) {
        Instant ahora = Instant.now();

        Paciente paciente = Paciente.builder()
                .nombreCompleto(request.nombreCompleto())
                .edad(request.edad())
                .frecuenciaCardiaca(request.frecuenciaCardiaca())
                .presionSistolica(request.presionSistolica())
                .presionDiastolica(request.presionDiastolica())
                .spo2(request.spo2())
                .temperatura(request.temperatura())
                .dolor(request.dolor())
                .gcs(request.gcs())
                .quejaPrincipal(request.quejaPrincipal())
                .estado(EstadoPaciente.REGISTRADO)
                .escalado(false)
                .fechaRegistro(ahora)
                .fechaUltimoCambioEstado(ahora)
                .build();

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
