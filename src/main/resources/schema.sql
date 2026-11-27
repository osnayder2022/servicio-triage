CREATE TABLE IF NOT EXISTS pacientes (
    id BIGSERIAL PRIMARY KEY,
    nombre_completo VARCHAR(255) NOT NULL,
    edad INTEGER NOT NULL,
    frecuencia_cardiaca INTEGER NOT NULL,
    presion_sistolica INTEGER NOT NULL,
    presion_diastolica INTEGER NOT NULL,
    spo2 INTEGER NOT NULL,
    temperatura NUMERIC(5, 2) NOT NULL,
    dolor INTEGER NOT NULL,
    gcs INTEGER NOT NULL,
    queja_principal TEXT NOT NULL,
    nivel_triage VARCHAR(30) NOT NULL,
    sla_minutos INTEGER NOT NULL,
    estado VARCHAR(30) NOT NULL,
    escalado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_registro TIMESTAMPTZ NOT NULL,
    fecha_ultimo_cambio_estado TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS auditoria_triage (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES pacientes(id),
    tipo_evento VARCHAR(50) NOT NULL,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30),
    detalle TEXT,
    fecha_evento TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_pacientes_sla_pendiente
    ON pacientes (fecha_registro)
    WHERE estado = 'REGISTRADO' AND escalado = FALSE;

-- Barrera adicional a nivel de BD: por paciente solo puede existir un escalamiento.
CREATE UNIQUE INDEX IF NOT EXISTS uq_auditoria_escalamiento_por_paciente
    ON auditoria_triage (paciente_id)
    WHERE tipo_evento = 'ESCALAMIENTO';
