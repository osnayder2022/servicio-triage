package com.vidacruz.triage.dominio.entidades;

import java.math.BigDecimal;
import java.time.Instant;

public record PacienteDetalleResponse(
        Long id,
        String nombreCompleto,
        Integer edad,
        Integer frecuenciaCardiaca,
        Integer presionSistolica,
        Integer presionDiastolica,
        Integer spo2,
        BigDecimal temperatura,
        Integer dolor,
        Integer gcs,
        String quejaPrincipal,
        NivelTriage nivelTriage,
        Integer slaMinutos,
        EstadoPaciente estado,
        Boolean escalado,
        Instant fechaRegistro,
        Instant fechaUltimoCambioEstado,
        Long tiempoEnEstadoActualSegundos
) {}
