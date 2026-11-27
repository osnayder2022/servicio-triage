package com.vidacruz.triage.infraestructura.adaptador.out.persistencia.proyecciones;

import java.math.BigDecimal;
import java.time.Instant;

public interface ReporteOperacionHoraProjection {
    Instant getHora();
    Long getTotalPacientes();
    Long getCriticos();
    Long getUrgentes();
    Long getUrgentes2();
    Long getNoUrgentes();
    Long getNoUrgentes2();
    BigDecimal getTiempoPromedioEsperaMinutos();
    BigDecimal getP95TiempoEsperaMinutos();
}
