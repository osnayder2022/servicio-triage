package com.vidacruz.triage.dominio.entidades;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public class Paciente {

    private static final BigDecimal TEMP_URGENTE = new BigDecimal("38.5");
    private static final BigDecimal TEMP_URGENTE_2 = new BigDecimal("38.0");

    // Inyectar desde propiedades o Datos de Referencia del documento
    private final Set<String> quejasAdministrativas = Set.of(
            "renovacion de formula",
            "certificado medico",
            "incapacidad",
            "orden de laboratorio"
    );

    private Long id;
    private String nombreCompleto;
    private Integer edad;
    private Integer frecuenciaCardiaca; // bpm
    private Integer presionSistolica; // mmHg
    private Integer presionDiastolica; // mmHg
    private Integer spo2; // %
    private BigDecimal temperatura; // °C
    private Integer dolor; // 0-10
    private Integer gcs; // 3-15
    private String quejaPrincipal;
    private NivelTriage nivelTriage;
    private Integer slaMinutos;
    private EstadoPaciente estado;
    private Boolean escalado;
    private Instant fechaRegistro; // Serializa a ISO 8601 (UTC)
    private Instant fechaUltimoCambioEstado;
    private Long version; // Control de concurrencia optimista (RN-03)

    public Paciente() {}

    public Paciente(Long id, String nombreCompleto, Integer edad, Integer frecuenciaCardiaca,
                    Integer presionSistolica, Integer presionDiastolica, Integer spo2,
                    BigDecimal temperatura, Integer dolor, Integer gcs, String quejaPrincipal,
                    NivelTriage nivelTriage, Integer slaMinutos, EstadoPaciente estado,
                    Boolean escalado, Instant fechaRegistro, Instant fechaUltimoCambioEstado,
                    Long version) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.edad = edad;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.presionSistolica = presionSistolica;
        this.presionDiastolica = presionDiastolica;
        this.spo2 = spo2;
        this.temperatura = temperatura;
        this.dolor = dolor;
        this.gcs = gcs;
        this.quejaPrincipal = quejaPrincipal;
        this.nivelTriage = nivelTriage;
        this.slaMinutos = slaMinutos;
        this.estado = estado;
        this.escalado = escalado;
        this.fechaRegistro = fechaRegistro;
        this.fechaUltimoCambioEstado = fechaUltimoCambioEstado;
        this.version = version;
    }

    private Paciente(Builder builder) {
        this.id = builder.id;
        this.nombreCompleto = builder.nombreCompleto;
        this.edad = builder.edad;
        this.frecuenciaCardiaca = builder.frecuenciaCardiaca;
        this.presionSistolica = builder.presionSistolica;
        this.presionDiastolica = builder.presionDiastolica;
        this.spo2 = builder.spo2;
        this.temperatura = builder.temperatura;
        this.dolor = builder.dolor;
        this.gcs = builder.gcs;
        this.quejaPrincipal = builder.quejaPrincipal;
        this.nivelTriage = builder.nivelTriage;
        this.slaMinutos = builder.slaMinutos;
        this.estado = builder.estado;
        this.escalado = builder.escalado;
        this.fechaRegistro = builder.fechaRegistro;
        this.fechaUltimoCambioEstado = builder.fechaUltimoCambioEstado;
        this.version = builder.version;
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

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Integer getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public Integer getPresionSistolica() {
        return presionSistolica;
    }

    public void setPresionSistolica(Integer presionSistolica) {
        this.presionSistolica = presionSistolica;
    }

    public Integer getPresionDiastolica() {
        return presionDiastolica;
    }

    public void setPresionDiastolica(Integer presionDiastolica) {
        this.presionDiastolica = presionDiastolica;
    }

    public Integer getSpo2() {
        return spo2;
    }

    public void setSpo2(Integer spo2) {
        this.spo2 = spo2;
    }

    public BigDecimal getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getDolor() {
        return dolor;
    }

    public void setDolor(Integer dolor) {
        this.dolor = dolor;
    }

    public Integer getGcs() {
        return gcs;
    }

    public void setGcs(Integer gcs) {
        this.gcs = gcs;
    }

    public String getQuejaPrincipal() {
        return quejaPrincipal;
    }

    public void setQuejaPrincipal(String quejaPrincipal) {
        this.quejaPrincipal = quejaPrincipal;
    }

    public NivelTriage getNivelTriage() {
        return nivelTriage;
    }

    public void setNivelTriage(NivelTriage nivelTriage) {
        this.nivelTriage = nivelTriage;
    }

    public Integer getSlaMinutos() {
        return slaMinutos;
    }

    public void setSlaMinutos(Integer slaMinutos) {
        this.slaMinutos = slaMinutos;
    }

    public EstadoPaciente getEstado() {
        return estado;
    }

    public void setEstado(EstadoPaciente estado) {
        this.estado = estado;
    }

    public Boolean getEscalado() {
        return escalado;
    }

    public void setEscalado(Boolean escalado) {
        this.escalado = escalado;
    }

    public Instant getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Instant fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Instant getFechaUltimoCambioEstado() {
        return fechaUltimoCambioEstado;
    }

    public void setFechaUltimoCambioEstado(Instant fechaUltimoCambioEstado) {
        this.fechaUltimoCambioEstado = fechaUltimoCambioEstado;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public static class Builder {
        private Long id;
        private String nombreCompleto;
        private Integer edad;
        private Integer frecuenciaCardiaca;
        private Integer presionSistolica;
        private Integer presionDiastolica;
        private Integer spo2;
        private BigDecimal temperatura;
        private Integer dolor;
        private Integer gcs;
        private String quejaPrincipal;
        private NivelTriage nivelTriage;
        private Integer slaMinutos;
        private EstadoPaciente estado;
        private Boolean escalado = false;
        private Instant fechaRegistro;
        private Instant fechaUltimoCambioEstado;
        private Long version;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            return this;
        }

        public Builder edad(Integer edad) {
            this.edad = edad;
            return this;
        }

        public Builder frecuenciaCardiaca(Integer frecuenciaCardiaca) {
            this.frecuenciaCardiaca = frecuenciaCardiaca;
            return this;
        }

        public Builder presionSistolica(Integer presionSistolica) {
            this.presionSistolica = presionSistolica;
            return this;
        }

        public Builder presionDiastolica(Integer presionDiastolica) {
            this.presionDiastolica = presionDiastolica;
            return this;
        }

        public Builder spo2(Integer spo2) {
            this.spo2 = spo2;
            return this;
        }

        public Builder temperatura(BigDecimal temperatura) {
            this.temperatura = temperatura;
            return this;
        }

        public Builder dolor(Integer dolor) {
            this.dolor = dolor;
            return this;
        }

        public Builder gcs(Integer gcs) {
            this.gcs = gcs;
            return this;
        }

        public Builder quejaPrincipal(String quejaPrincipal) {
            this.quejaPrincipal = quejaPrincipal;
            return this;
        }

        public Builder nivelTriage(NivelTriage nivelTriage) {
            this.nivelTriage = nivelTriage;
            return this;
        }

        public Builder slaMinutos(Integer slaMinutos) {
            this.slaMinutos = slaMinutos;
            return this;
        }

        public Builder estado(EstadoPaciente estado) {
            this.estado = estado;
            return this;
        }

        public Builder escalado(Boolean escalado) {
            this.escalado = escalado;
            return this;
        }

        public Builder fechaRegistro(Instant fechaRegistro) {
            this.fechaRegistro = fechaRegistro;
            return this;
        }

        public Builder fechaUltimoCambioEstado(Instant fechaUltimoCambioEstado) {
            this.fechaUltimoCambioEstado = fechaUltimoCambioEstado;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public Paciente build() {
            Objects.requireNonNull(nombreCompleto, "El nombre completo del paciente es obligatorio");
            if (fechaRegistro == null) {
                fechaRegistro = Instant.now();
            }
            if (fechaUltimoCambioEstado == null) {
                fechaUltimoCambioEstado = fechaRegistro;
            }
            return new Paciente(this);
        }
    }



    public NivelTriage clasificar( ) {

        // Nivel 1: CRITICO
        if (this.frecuenciaCardiaca < 40 || this.frecuenciaCardiaca > 120 || this.spo2 < 90 || this.presionSistolica < 90 || this.gcs <= 8) {
            return NivelTriage.CRITICO;
        }

        // Nivel 2: URGENTE
        if ((this.spo2 >= 90 && this.spo2 <= 93)
                || (this.presionSistolica >= 90 && this.presionSistolica <= 100)
                || this.temperatura.compareTo(TEMP_URGENTE) >= 0
                || (this.gcs >= 9 && this.gcs <= 13)) {
            return NivelTriage.URGENTE;
        }

        // Nivel 3: URGENTE_2
        if ((this.frecuenciaCardiaca >= 90 && this.frecuenciaCardiaca <= 120)
                || (this.temperatura.compareTo(TEMP_URGENTE_2) >= 0 && this.temperatura.compareTo(TEMP_URGENTE) < 0)
                || this.dolor >= 7) {
            return NivelTriage.URGENTE_2;
        }

        // Nivel 5: NO_URGENTE_2 (clasifica como nivel 4 + queja administrativa)
        if (esQuejaAdministrativa(this.getQuejaPrincipal())) {
            return NivelTriage.NO_URGENTE_2;
        }

        // Nivel 4: NO_URGENTE
        return NivelTriage.NO_URGENTE;
    }

    private boolean esQuejaAdministrativa(String quejaPrincipal) {
        if (quejaPrincipal == null || quejaPrincipal.isBlank()) {
            return false;
        }
        String normalizada = quejaPrincipal.trim().toLowerCase();
        return quejasAdministrativas.contains(normalizada);
    }

    public boolean esTransicionValida(EstadoPaciente nuevoEstado) {
        return switch (this.estado) {
            case REGISTRADO -> nuevoEstado == EstadoPaciente.EN_CONSULTA || nuevoEstado == EstadoPaciente.ALTA;
            case EN_CONSULTA -> nuevoEstado == EstadoPaciente.ALTA;
            case ALTA -> false;
        };
    }
}