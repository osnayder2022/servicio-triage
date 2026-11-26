package com.vidacruz.triage.infraestructura.adaptador.out.persistencia.repositorios;

import com.vidacruz.triage.infraestructura.adaptador.out.persistencia.entidades.PacienteEntidad;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PacienteRepositorio extends R2dbcRepository<PacienteEntidad, Long> {

    /**
     * Busca pacientes en estado REGISTRADO, aún no escalados,
     * cuyo tiempo desde fecha_registro ya superó su sla_minutos (PostgreSQL).
     */
    @Query("""
        SELECT * FROM pacientes
        WHERE estado = 'REGISTRADO'
          AND escalado = FALSE
          AND NOW() >= fecha_registro + (sla_minutos * INTERVAL '1 minute')
        """)
    Flux<PacienteEntidad> findPacientesConSlaVencido();

    /**
     * Actualización atómica e idempotente (RN-03):
     * Solo actualiza si sigue en REGISTRADO y escalado = FALSE.
     * Retorna 1 si se escaló en este intento, 0 si ya fue escalado o cambió de estado.
     */
    @Modifying
    @Query("""
        UPDATE pacientes
        SET escalado = TRUE,
            version = version + 1
        WHERE id = :id
          AND estado = 'REGISTRADO'
          AND escalado = FALSE
        """)
    Mono<Integer> marcarComoEscaladoSiAplica(Long id);
}
