package com.vidacruz.triage.infraestructura.adaptador.out.configuracion;

import com.vidacruz.triage.infraestructura.config.TriageProperties;
import com.vidacruz.triage.dominio.port.out.SlaTriagePort;
import com.vidacruz.triage.dominio.entidades.NivelTriage;
import org.springframework.stereotype.Component;

@Component
public class SlaTriagePropertiesAdapter implements SlaTriagePort {

    private final TriageProperties triageProperties;

    public SlaTriagePropertiesAdapter(TriageProperties triageProperties) {
        this.triageProperties = triageProperties;
    }

    @Override
    public Integer obtenerSla(NivelTriage nivel) {
        return triageProperties.obtenerSla(nivel);
    }
}
