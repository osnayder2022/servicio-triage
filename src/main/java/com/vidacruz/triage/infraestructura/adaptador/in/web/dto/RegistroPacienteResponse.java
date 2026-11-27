package com.vidacruz.triage.infraestructura.adaptador.in.web.dto;

import com.vidacruz.triage.dominio.entidades.EstadoPaciente;
import com.vidacruz.triage.dominio.entidades.NivelTriage;

import java.time.Instant;

public record RegistroPacienteResponse(
        Long id,
        NivelTriage nivelClasificado,
        Integer slaMinutos,
        EstadoPaciente estado,
        Instant fechaRegistro
) {}
