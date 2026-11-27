package com.vidacruz.triage.infraestructura.adaptador.in.web.controlador;

import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.RegistroPacienteResponse;
import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.PacienteDetalleResponse;
import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.RegistroPacienteRequest;
import com.vidacruz.triage.aplicacion.casouso.port.in.GestionarEstadoPacienteCasoUso;
import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.CambioEstadoRequest;
import com.vidacruz.triage.aplicacion.casouso.port.in.RegistrarPacienteCasoUso;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/triage/pacientes")
@RequiredArgsConstructor
@Validated
public class TriageControlador {
    private final GestionarEstadoPacienteCasoUso gestionarEstadoPacienteCasoUso;
    private final RegistrarPacienteCasoUso registrarPacienteCasoUso;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RegistroPacienteResponse> registrarPaciente(
            @Valid @RequestBody RegistroPacienteRequest request) {
        return registrarPacienteCasoUso.registrar(request);
    }

    @PatchMapping("/{id}/estado")
    public Mono<PacienteDetalleResponse> cambiarEstado(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CambioEstadoRequest request) {
        return gestionarEstadoPacienteCasoUso.cambiarEstado(id, request.estado());
    }

    @GetMapping("/{id}")
    public Mono<PacienteDetalleResponse> obtenerPaciente(@PathVariable @Positive Long id) {
        return gestionarEstadoPacienteCasoUso.obtenerPorId(id);
    }
}
