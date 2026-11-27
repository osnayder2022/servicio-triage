package com.vidacruz.triage.dominio.port.out;

import com.vidacruz.triage.dominio.entidades.NivelTriage;

public interface SlaTriagePort {
    Integer obtenerSla(NivelTriage nivel);
}
