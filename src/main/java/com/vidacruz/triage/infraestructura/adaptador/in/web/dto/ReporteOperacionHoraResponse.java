package com.vidacruz.triage.infraestructura.adaptador.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ReporteOperacionHoraResponse(
        Instant hora,
        Long totalPacientes,
        Long criticos,
        Long urgentes,
        Long urgentes2,
        Long noUrgentes,
        Long noUrgentes2,
        BigDecimal tiempoPromedioEsperaMinutos,
        BigDecimal p95TiempoEsperaMinutos
) {}
