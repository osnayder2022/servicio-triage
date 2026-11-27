package com.vidacruz.triage.infraestructura.adaptador.out.persistencia;

import com.vidacruz.triage.dominio.port.out.ReporteOperacionPort;
import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.ReporteOperacionHoraResponse;
import com.vidacruz.triage.infraestructura.adaptador.out.persistencia.repositorios.PacienteRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ReporteOperacionAdaptadorPersistencia implements ReporteOperacionPort {
    private final PacienteRepositorio pacienteRepositorio;

    @Override
    public Flux<ReporteOperacionHoraResponse> consultarPorHora(Instant desde, Instant hasta) {
        return pacienteRepositorio.generarReporteOperacion(desde, hasta)
                .map(resultado -> new ReporteOperacionHoraResponse(
                        resultado.getHora(), resultado.getTotalPacientes(), resultado.getCriticos(),
                        resultado.getUrgentes(), resultado.getUrgentes2(), resultado.getNoUrgentes(),
                        resultado.getNoUrgentes2(), resultado.getTiempoPromedioEsperaMinutos(),
                        resultado.getP95TiempoEsperaMinutos()));
    }
}
