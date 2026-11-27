package com.vidacruz.triage.aplicacion.casouso.port.in;

import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.PacienteDetalleResponse;
import com.vidacruz.triage.dominio.entidades.EstadoPaciente;
import reactor.core.publisher.Mono;

public interface GestionarEstadoPacienteCasoUso {
    Mono<PacienteDetalleResponse> cambiarEstado(Long pacienteId, EstadoPaciente nuevoEstado);
    Mono<PacienteDetalleResponse> obtenerPorId(Long pacienteId);
}
