package com.vidacruz.triage.aplicacion.casouso.port.in;

import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.RegistroPacienteRequest;
import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.RegistroPacienteResponse;
import reactor.core.publisher.Mono;

public interface RegistrarPacienteCasoUso {
    Mono<RegistroPacienteResponse> registrar(RegistroPacienteRequest request);
}
