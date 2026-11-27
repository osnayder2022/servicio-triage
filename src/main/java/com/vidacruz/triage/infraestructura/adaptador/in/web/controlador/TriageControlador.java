package com.vidacruz.triage.infraestructura.adaptador.in.web.controlador;

import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.RegistroPacienteRequest;
import com.vidacruz.triage.aplicacion.casouso.port.in.GestionarEstadoPacienteCasoUso;
import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.CambioEstadoRequest;
import com.vidacruz.triage.aplicacion.casouso.port.in.RegistrarPacienteCasoUso;
import com.vidacruz.triage.dominio.entidades.RegistroPacienteResponse;
import com.vidacruz.triage.dominio.entidades.PacienteDetalleResponse;
import org.springframework.validation.annotation.Validated;
import com.vidacruz.triage.dominio.entidades.Paciente;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/triage/pacientes")
public class TriageControlador {
    private final GestionarEstadoPacienteCasoUso gestionarEstadoPacienteCasoUso;
    private final RegistrarPacienteCasoUso registrarPacienteCasoUso;
    private final ModelMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RegistroPacienteResponse> registrarPaciente(
            @Valid @RequestBody RegistroPacienteRequest request) {
        Paciente paciente = mapper.map(request, Paciente.class);
        return registrarPacienteCasoUso.registrar(paciente)
                .doOnSubscribe(ignorado -> log.info("Solicitud de registro de paciente recibida"))
                .doOnSuccess(respuesta -> log.info("Paciente {} registrado con nivel {} y SLA {} min",
                        respuesta.id(), respuesta.nivelClasificado(), respuesta.slaMinutos()))
                .doOnError(error -> log.error("Error al registrar paciente: {}", error.getMessage(), error));
    }

    @PatchMapping("/{id}/estado")
    public Mono<PacienteDetalleResponse> cambiarEstado(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CambioEstadoRequest request) {
        return gestionarEstadoPacienteCasoUso.cambiarEstado(id, request.estado())
                .doOnSubscribe(ignorado -> log.info("Solicitud de cambio de estado para paciente {} a {}", id, request.estado()))
                .doOnSuccess(respuesta -> log.info("Estado del paciente {} actualizado a {}", id, respuesta.estado()))
                .doOnError(error -> log.error("Error al cambiar estado del paciente {}: {}", id, error.getMessage(), error));
    }

    @GetMapping("/{id}")
    public Mono<PacienteDetalleResponse> obtenerPaciente(@PathVariable @Positive Long id) {
        return gestionarEstadoPacienteCasoUso.obtenerPorId(id)
                .doOnSubscribe(ignorado -> log.info("Consulta de paciente {} recibida", id))
                .doOnSuccess(respuesta -> log.info("Consulta de paciente {} completada", id))
                .doOnError(error -> log.error("Error al consultar paciente {}: {}", id, error.getMessage(), error));
    }
}
