package com.vidacruz.triage.infraestructura.adaptador.out.persistencia.repositorios;

import com.vidacruz.triage.infraestructura.adaptador.out.persistencia.entidades.PacienteEntidad;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.vidacruz.triage.infraestructura.adaptador.out.persistencia.proyecciones.ReporteOperacionHoraProjection;
import java.time.Instant;

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

    @Query("""
        WITH pacientes_rango AS (
            SELECT p.id, p.fecha_registro, p.nivel_triage
            FROM pacientes p
            WHERE p.fecha_registro >= :desde AND p.fecha_registro < :hasta
        ), totales AS (
            SELECT date_trunc('hour', fecha_registro) AS hora,
                   COUNT(*) AS total_pacientes,
                   COUNT(*) FILTER (WHERE nivel_triage = 'CRITICO') AS criticos,
                   COUNT(*) FILTER (WHERE nivel_triage = 'URGENTE') AS urgentes,
                   COUNT(*) FILTER (WHERE nivel_triage = 'URGENTE_2') AS urgentes2,
                   COUNT(*) FILTER (WHERE nivel_triage = 'NO_URGENTE') AS no_urgentes,
                   COUNT(*) FILTER (WHERE nivel_triage = 'NO_URGENTE_2') AS no_urgentes2
            FROM pacientes_rango GROUP BY date_trunc('hour', fecha_registro)
        ), esperas AS (
            SELECT date_trunc('hour', p.fecha_registro) AS hora,
                   EXTRACT(EPOCH FROM (consulta.fecha_entrada - p.fecha_registro)) / 60.0 AS espera_minutos
            FROM pacientes_rango p
            CROSS JOIN LATERAL (
                SELECT MIN(a.fecha_evento) AS fecha_entrada
                FROM auditoria_triage a
                WHERE a.paciente_id = p.id AND a.tipo_evento = 'ENTRADA_A_CONSULTA'
            ) consulta
            WHERE consulta.fecha_entrada IS NOT NULL
        ), esperas_rankeadas AS (
            SELECT hora, espera_minutos,
                   ROW_NUMBER() OVER (PARTITION BY hora ORDER BY espera_minutos) AS posicion,
                   COUNT(*) OVER (PARTITION BY hora) AS cantidad
            FROM esperas
        ), metricas_espera AS (
            SELECT hora,
                   AVG(espera_minutos) AS tiempo_promedio_espera_minutos,
                   MAX(espera_minutos) FILTER (WHERE posicion = CEIL(cantidad * 0.95)) AS p95_tiempo_espera_minutos
            FROM esperas_rankeadas GROUP BY hora
        )
        SELECT t.hora, t.total_pacientes, t.criticos, t.urgentes, t.urgentes2,
               t.no_urgentes, t.no_urgentes2, m.tiempo_promedio_espera_minutos,
               m.p95_tiempo_espera_minutos
        FROM totales t LEFT JOIN metricas_espera m ON m.hora = t.hora
        ORDER BY t.hora
        """)
    Flux<ReporteOperacionHoraProjection> generarReporteOperacion(Instant desde, Instant hasta);
}
