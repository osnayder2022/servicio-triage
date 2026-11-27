package com.vidacruz.triage.infraestructura.adaptador.out.persistencia;

import com.vidacruz.triage.infraestructura.adaptador.out.persistencia.repositorios.PacienteRepositorio;
import com.vidacruz.triage.infraestructura.adaptador.out.persistencia.entidades.PacienteEntidad;
import com.vidacruz.triage.dominio.port.out.PacienteRepositorioPort;
import com.vidacruz.triage.dominio.entidades.Paciente;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PacienteAdaptadorPersistencia implements PacienteRepositorioPort {

    private final PacienteRepositorio pacienteRepositorio;
    private final ModelMapper mapper;

    @Override
    public Mono<Paciente> guardar(Paciente paciente) {
        return Mono.fromCallable(() -> mapper.map(paciente, PacienteEntidad.class))
                .flatMap(pacienteRepositorio::save)
                .map(entidad -> mapper.map(entidad, Paciente.class));
    }

    @Override
    public Flux<Paciente> buscarPacientesConSlaVencido() {
        return pacienteRepositorio.findPacientesConSlaVencido().map(entidad -> mapper.map(entidad,Paciente.class));
    }

    @Override
    public Mono<Integer> marcarComoEscaladoSiAplica(Long pacienteId) {
        return pacienteRepositorio.marcarComoEscaladoSiAplica(pacienteId);
    }

    @Override
    public Mono<Paciente> buscarPorId(Long pacienteId) {
        return pacienteRepositorio.findById(pacienteId).map(entidad -> mapper.map(entidad,Paciente.class));
    }
}
