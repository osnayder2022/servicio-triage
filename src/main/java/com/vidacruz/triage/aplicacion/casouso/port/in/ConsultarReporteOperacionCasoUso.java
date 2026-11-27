package com.vidacruz.triage.aplicacion.casouso.port.in;

import com.vidacruz.triage.dominio.entidades.ReporteOperacionHoraResponse;
import reactor.core.publisher.Flux;

import java.time.Instant;

public interface ConsultarReporteOperacionCasoUso {
    Flux<ReporteOperacionHoraResponse> consultar(Instant desde, Instant hasta);
}
