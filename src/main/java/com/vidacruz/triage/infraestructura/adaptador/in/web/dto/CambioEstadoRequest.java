package com.vidacruz.triage.infraestructura.adaptador.in.web.dto;

import com.vidacruz.triage.dominio.entidades.EstadoPaciente;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        EstadoPaciente estado
) {}
