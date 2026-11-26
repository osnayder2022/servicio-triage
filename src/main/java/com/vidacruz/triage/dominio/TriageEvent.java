package com.vidacruz.triage.dominio;

import java.time.Instant;

public record TriageEvent(
        String tipoEvento,        // ej. "paciente_escalado"
        Long pacienteId,
        NivelTriage nivelTriage,
        EstadoPaciente estadoActual,
        Integer slaMinutos,
        Instant fechaEvento
) {}
