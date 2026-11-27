package com.vidacruz.triage.aplicacion.service;

import com.vidacruz.triage.aplicacion.casouso.port.in.ConsultarReporteOperacionCasoUso;
import com.vidacruz.triage.dominio.port.out.ReporteOperacionPort;
import com.vidacruz.triage.dominio.entidades.ReporteOperacionHoraResponse;
import reactor.core.publisher.Flux;

import java.time.Instant;

public class ReporteOperacionServiceImpl implements ConsultarReporteOperacionCasoUso {
    private final ReporteOperacionPort reporteOperacionPort;

    public ReporteOperacionServiceImpl(ReporteOperacionPort reporteOperacionPort) {
        this.reporteOperacionPort = reporteOperacionPort;
    }

    @Override
    public Flux<ReporteOperacionHoraResponse> consultar(Instant desde, Instant hasta) {
        return reporteOperacionPort.consultarPorHora(desde, hasta);
    }
}
