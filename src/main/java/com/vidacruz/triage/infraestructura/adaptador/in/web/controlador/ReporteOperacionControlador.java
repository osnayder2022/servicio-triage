package com.vidacruz.triage.infraestructura.adaptador.in.web.controlador;

import com.vidacruz.triage.aplicacion.casouso.port.in.ConsultarReporteOperacionCasoUso;
import com.vidacruz.triage.dominio.entidades.ReporteOperacionHoraResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Validated
@RestController
@RequestMapping("/triage/reportes")
@RequiredArgsConstructor
public class ReporteOperacionControlador {
    private final ConsultarReporteOperacionCasoUso consultarReporteOperacionCasoUso;

    @GetMapping("/operacion")
    public Flux<ReporteOperacionHoraResponse> consultarOperacion(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta) {
        if (!desde.isBefore(hasta)) {
            log.warn("Rango inválido solicitado para reporte de operación: desde={}, hasta={}", desde, hasta);
            return Flux.error(new IllegalArgumentException("'desde' debe ser anterior a 'hasta'"));
        }
        return Flux.defer(() -> {
            AtomicInteger horasReportadas = new AtomicInteger();
            log.info("Consulta de reporte de operación iniciada: desde={}, hasta={}", desde, hasta);

            return consultarReporteOperacionCasoUso.consultar(desde, hasta)
                    .doOnNext(reporte -> {
                        horasReportadas.incrementAndGet();
                        log.info("Reporte de operación generado para hora {}: totalPacientes={}",
                                reporte.hora(), reporte.totalPacientes());
                    })
                    .doOnComplete(() -> log.info(
                            "Consulta de reporte de operación completada: desde={}, hasta={}, horas={}",
                            desde, hasta, horasReportadas.get()))
                    .doOnError(error -> log.error(
                            "Error al consultar reporte de operación: desde={}, hasta={}, mensaje={}",
                            desde, hasta, error.getMessage(), error));
        });
    }
}
