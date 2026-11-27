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
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;

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
        return registrarPacienteCasoUso.registrar(paciente);
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
