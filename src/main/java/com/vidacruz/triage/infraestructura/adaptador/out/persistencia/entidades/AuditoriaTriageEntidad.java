package com.vidacruz.triage.infraestructura.adaptador.out.persistencia.entidades;

import com.vidacruz.triage.dominio.entidades.EstadoPaciente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("auditoria_triage")
public class AuditoriaTriageEntidad {

    @Id
    private Long id;

    @Column("paciente_id")
    private Long pacienteId;

    @Column("tipo_evento")
    private String tipoEvento; // ej. REGISTRO, CAMBIO_ESTADO, ESCALAMIENTO_SLA

    @Column("estado_anterior")
    private EstadoPaciente estadoAnterior;

    @Column("estado_nuevo")
    private EstadoPaciente estadoNuevo;

    @Column("detalle")
    private String detalle;

    @Column("fecha_evento")
    private Instant fechaEvento;
}
