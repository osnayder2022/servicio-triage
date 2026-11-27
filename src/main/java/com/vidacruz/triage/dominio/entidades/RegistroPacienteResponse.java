package com.vidacruz.triage.dominio.entidades;

import java.time.Instant;

public record RegistroPacienteResponse(
        Long id,
        NivelTriage nivelClasificado,
        Integer slaMinutos,
        EstadoPaciente estado,
        Instant fechaRegistro
) {}
