package com.vidacruz.triage.aplicacion.casouso.port.in;

import com.vidacruz.triage.dominio.entidades.RegistroPacienteResponse;
import com.vidacruz.triage.dominio.entidades.Paciente;
import reactor.core.publisher.Mono;

public interface RegistrarPacienteCasoUso {
    Mono<RegistroPacienteResponse> registrar(Paciente paciente);
}
