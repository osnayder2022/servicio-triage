package com.vidacruz.triage.infraestructura.config;

import com.vidacruz.triage.dominio.NivelTriage;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "triage")
public class TriageProperties {

    private Map<NivelTriage, Integer> slaMinutos = new EnumMap<>(NivelTriage.class);
    private SchedulerProperties scheduler = new SchedulerProperties();

    public Integer obtenerSla(NivelTriage nivel) {
        return slaMinutos.getOrDefault(nivel, 60);
    }

    @Data
    public static class SchedulerProperties {
        private long intervaloEvaluacionMs = 15000L;
    }
}
