package com.vidacruz.triage.dominio.port.out;

import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.ReporteOperacionHoraResponse;
import reactor.core.publisher.Flux;

import java.time.Instant;

public interface ReporteOperacionPort {
    Flux<ReporteOperacionHoraResponse> consultarPorHora(Instant desde, Instant hasta);
}
