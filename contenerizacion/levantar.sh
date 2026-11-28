#!/usr/bin/env bash
set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
ENV_FILE="${SCRIPT_DIR}/.env"
COMPOSE_FILE="${SCRIPT_DIR}/docker-compose.yml"

fail() {
  echo "Error: $*" >&2
  exit 1
}

command -v docker >/dev/null 2>&1 || fail "Docker debe estar instalado."
docker compose version >/dev/null 2>&1 || fail "Docker Compose v2 debe estar disponible."
[[ -f "${ENV_FILE}" ]] || fail "No existe ${ENV_FILE}. Cree el archivo con las variables requeridas."

required_variables=(
  TRIAGE_DB_NAME
  TRIAGE_DB_USER
  TRIAGE_DB_PASSWORD
  TRIAGE_KAFKA_CLUSTER_ID
)

for variable in "${required_variables[@]}"; do
  value="$(grep -E "^${variable}=" "${ENV_FILE}" | head -n 1 | cut -d= -f2- || true)"
  [[ -n "${value}" ]] || fail "Configure ${variable} con un valor no vacío en ${ENV_FILE}."
  [[ "${value}" != REEMPLACE_* ]] || fail "Reemplace el valor temporal de ${variable} en ${ENV_FILE}."
done

echo "Compilando y ejecutando pruebas del proyecto..."
cd "${PROJECT_DIR}"
chmod +x mvnw
./mvnw clean package

echo "Validando la configuración de Docker Compose..."
docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}" config --quiet

echo "Construyendo la imagen Docker del servicio de triage..."
docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}" build servicio-triage

echo "Levantando PostgreSQL, Kafka y el servicio de triage..."
docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}" up -d --no-build --force-recreate

echo "Servicio disponible en http://localhost:8081"
docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}" ps
