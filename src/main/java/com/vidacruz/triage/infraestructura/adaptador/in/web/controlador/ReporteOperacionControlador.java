package com.vidacruz.triage.infraestructura.adaptador.in.web.controlador;

import com.vidacruz.triage.aplicacion.casouso.port.in.ConsultarReporteOperacionCasoUso;
import com.vidacruz.triage.dominio.entidades.ReporteOperacionHoraResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Instant;

@RestController
@RequestMapping("/triage/reportes")
@RequiredArgsConstructor
@Validated
public class ReporteOperacionControlador {
    private final ConsultarReporteOperacionCasoUso consultarReporteOperacionCasoUso;

    @GetMapping("/operacion")
    public Flux<ReporteOperacionHoraResponse> consultarOperacion(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta) {
        if (!desde.isBefore(hasta)) {
            return Flux.error(new IllegalArgumentException("'desde' debe ser anterior a 'hasta'"));
        }
        return consultarReporteOperacionCasoUso.consultar(desde, hasta);
    }
}
