# Servicio de triage

Microservicio reactivo para registrar y gestionar pacientes en un proceso de triage. Clasifica cada paciente según sus signos vitales, asigna un nivel y un tiempo máximo de atención (SLA), persiste el historial de cambios y publica eventos para que otros servicios puedan reaccionar. También supervisa periódicamente los casos que exceden el SLA y expone reportes operativos por hora.

## Funcionalidades principales

- Registra pacientes y calcula su nivel de triage y SLA.
- Consulta el detalle de un paciente y actualiza su estado (`REGISTRADO`, `EN_CONSULTA` o `ALTA`).
- Guarda una auditoría de los eventos y publica eventos de triage en Kafka en formato JSON.
- Detecta y escala pacientes que superan el SLA configurado.
- Genera reportes de operación por intervalo horario.

## Tecnologías

- Java 17 y Spring Boot 3.
- Spring WebFlux y Reactor para programación reactiva/no bloqueante.
- Spring Data R2DBC y PostgreSQL 17 para persistencia reactiva.
- Apache Kafka 3.9 para mensajería de eventos.
- Maven y JUnit 5 para compilación y pruebas.
- Docker y Docker Compose para ejecutar el entorno completo.

La aplicación está organizada con puertos y adaptadores: el dominio y los casos de uso permanecen separados de los adaptadores HTTP, PostgreSQL y Kafka.

## API principal

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/triage/pacientes` | Registra y clasifica un paciente. |
| `GET` | `/triage/pacientes/{id}` | Consulta un paciente por identificador. |
| `PATCH` | `/triage/pacientes/{id}/estado` | Actualiza el estado del paciente. |
| `GET` | `/triage/reportes/operacion?desde={ISO-8601}&hasta={ISO-8601}` | Genera el reporte operativo por hora. |

## Levantar la aplicación

### Requisitos

- Docker Desktop en ejecución, con Docker Compose v2.
- Bash. En Windows puede usar Git Bash.

### 1. Crear la configuración de entorno

En `contenerizacion`, cree el archivo `.env` a partir de este ejemplo:

```dotenv
TRIAGE_DB_NAME=triage_db
TRIAGE_DB_USER=postgres
TRIAGE_DB_PASSWORD=una_clave_segura
TRIAGE_KAFKA_CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk
```

> `TRIAGE_KAFKA_CLUSTER_ID` debe ser un UUID válido de Kafka en formato Base64 URL. Puede generarlo con `docker run --rm apache/kafka:3.9.0 /opt/kafka/bin/kafka-storage.sh random-uuid`.

### 2. Ejecutar el script

Desde la raíz del repositorio:

```bash
bash contenerizacion/levantar.sh
```

El script valida las variables requeridas, ejecuta `./mvnw clean package`, valida Docker Compose, construye la imagen y levanta PostgreSQL, Kafka y el microservicio.

Al finalizar, el servicio queda disponible en `http://localhost:8081`. Los puertos de las dependencias son `5432` para PostgreSQL y `29092` para Kafka.

### Detener el entorno

```bash
docker compose --env-file contenerizacion/.env -f contenerizacion/docker-compose.yml down
```

Los datos se conservan en volúmenes de Docker. Para eliminarlos también, agregue la opción `--volumes`.

## Despliegue en Kubernetes

Los manifiestos se encuentran en `kubernetes/`, separados por componente. Antes de desplegar, reemplace `CAMBIAR_EN_PRODUCCION` y `REEMPLAZAR_POR_UUID_KAFKA_VALIDO` en los secretos. La imagen configurada para el servicio es `ghcr.io/tu-organizacion/servicio-triage:latest`; publíquela en un registro accesible por el clúster o cambie ese valor en `kubernetes/servicio-triage/deployment.yaml`.

```bash
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/postgres/
kubectl apply -f kubernetes/kafka/
kubectl apply -f kubernetes/servicio-triage/
```

El Ingress expone la API bajo el host `triage.local` y requiere un controlador de Ingress NGINX. PostgreSQL y Kafka se mantienen como servicios internos (`ClusterIP`), ya que un Ingress estándar solo enruta tráfico HTTP/HTTPS.

## Decisiones de arquitectura

La decisión sobre arquitectura reactiva, integración con Kafka y despliegue en Kubernetes se documenta en [ADR 0001](docs/adr/0001-arquitectura-reactiva-y-despliegue-kubernetes.md).
