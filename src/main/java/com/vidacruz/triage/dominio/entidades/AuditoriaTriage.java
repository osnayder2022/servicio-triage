package com.vidacruz.triage.dominio.entidades;

import java.time.Instant;
import java.util.Objects;

public class AuditoriaTriage {
    private Long id;
    private Long pacienteId;
    private String tipoEvento;
    private EstadoPaciente estadoAnterior;
    private EstadoPaciente estadoNuevo;
    private String detalle;
    private Instant fechaEvento;

    public AuditoriaTriage() {
    }

    public AuditoriaTriage(Long id, Long pacienteId, String tipoEvento, EstadoPaciente estadoAnterior,
                           EstadoPaciente estadoNuevo, String detalle, Instant fechaEvento) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.tipoEvento = tipoEvento;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.detalle = detalle;
        this.fechaEvento = fechaEvento;
    }

    private AuditoriaTriage(Builder builder) {
        this.id = builder.id;
        this.pacienteId = builder.pacienteId;
        this.tipoEvento = builder.tipoEvento;
        this.estadoAnterior = builder.estadoAnterior;
        this.estadoNuevo = builder.estadoNuevo;
        this.detalle = builder.detalle;
        this.fechaEvento = builder.fechaEvento;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public EstadoPaciente getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoPaciente estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoPaciente getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoPaciente estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Instant getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(Instant fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public static class Builder {
        private Long id;
        private Long pacienteId;
        private String tipoEvento;
        private EstadoPaciente estadoAnterior;
        private EstadoPaciente estadoNuevo;
        private String detalle;
        private Instant fechaEvento;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder pacienteId(Long pacienteId) {
            this.pacienteId = pacienteId;
            return this;
        }

        public Builder tipoEvento(String tipoEvento) {
            this.tipoEvento = tipoEvento;
            return this;
        }

        public Builder estadoAnterior(EstadoPaciente estadoAnterior) {
            this.estadoAnterior = estadoAnterior;
            return this;
        }

        public Builder estadoNuevo(EstadoPaciente estadoNuevo) {
            this.estadoNuevo = estadoNuevo;
            return this;
        }

        public Builder detalle(String detalle) {
            this.detalle = detalle;
            return this;
        }

        public Builder fechaEvento(Instant fechaEvento) {
            this.fechaEvento = fechaEvento;
            return this;
        }

        public AuditoriaTriage build() {
            Objects.requireNonNull(pacienteId, "El ID del paciente es obligatorio en la auditoría");
            Objects.requireNonNull(tipoEvento, "El tipo de evento es obligatorio en la auditoría");
            if (fechaEvento == null) {
                fechaEvento = Instant.now();
            }
            return new AuditoriaTriage(this);
        }
    }
}