package com.vidacruz.triage.infraestructura.config;

import com.vidacruz.triage.infraestructura.adaptador.in.web.dto.PacienteDetalleResponse;
import org.springframework.context.annotation.Configuration;
import com.vidacruz.triage.dominio.entidades.Paciente;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;
import org.modelmapper.Converter;
import java.time.Duration;
import java.time.Instant;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        Converter<Paciente, PacienteDetalleResponse> pacienteToResponseConverter = ctx -> {
            Paciente src = ctx.getSource();
            if (src == null) {
                return null;
            }

            Long tiempoEnEstadoActualSegundos = src.getFechaUltimoCambioEstado() != null
                    ? Duration.between(src.getFechaUltimoCambioEstado(), Instant.now()).getSeconds()
                    : null;

            return new PacienteDetalleResponse(
                    src.getId(),
                    src.getNombreCompleto(),
                    src.getEdad(),
                    src.getFrecuenciaCardiaca(),
                    src.getPresionSistolica(),
                    src.getPresionDiastolica(),
                    src.getSpo2(),
                    src.getTemperatura(),
                    src.getDolor(),
                    src.getGcs(),
                    src.getQuejaPrincipal(),
                    src.getNivelTriage(),
                    src.getSlaMinutos(),
                    src.getEstado(),
                    src.getEscalado(),
                    src.getFechaRegistro(),
                    src.getFechaUltimoCambioEstado(),
                    tiempoEnEstadoActualSegundos
            );
        };

        modelMapper.addConverter(pacienteToResponseConverter);
        return modelMapper;
    }
}