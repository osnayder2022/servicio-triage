package com.vidacruz.triage.infraestructura.adaptador.out.persistencia.entidades;

import com.vidacruz.triage.dominio.EstadoPaciente;
import com.vidacruz.triage.dominio.NivelTriage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("pacientes")
public class PacienteEntidad {

    @Id
    private Long id;

    @Column("nombre_completo")
    private String nombreCompleto;

    @Column("edad")
    private Integer edad;

    @Column("frecuencia_cardiaca")
    private Integer frecuenciaCardiaca; // bpm

    @Column("presion_sistolica")
    private Integer presionSistolica; // mmHg

    @Column("presion_diastolica")
    private Integer presionDiastolica; // mmHg

    @Column("spo2")
    private Integer spo2; // %

    @Column("temperatura")
    private BigDecimal temperatura; // °C

    @Column("dolor")
    private Integer dolor; // 0-10

    @Column("gcs")
    private Integer gcs; // 3-15

    @Column("queja_principal")
    private String quejaPrincipal;

    @Column("nivel_triage")
    private NivelTriage nivelTriage;

    @Column("sla_minutos")
    private Integer slaMinutos;

    @Column("estado")
    private EstadoPaciente estado;

    @Column("escalado")
    private Boolean escalado;

    @Column("fecha_registro")
    private Instant fechaRegistro; // Serializa a ISO 8601 (UTC)

    @Column("fecha_ultimo_cambio_estado")
    private Instant fechaUltimoCambioEstado;

    @Version
    @Column("version")
    private Long version; // Control de concurrencia optimista (RN-03)
}