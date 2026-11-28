# ADR 0001 Arquitectura reactiva y despliegue en Kubernetes

- Estado: Aceptado
- Fecha: 2026-11-27

## Contexto

VidaCruz necesita un servicio que reciba pacientes, los clasifique con base en sus signos vitales, asigne el SLA correspondiente y conserve el historial de sus cambios de estado. El servicio debe publicar eventos de triage para integrarse con otros consumidores y escalar los pacientes que excedan su SLA.

La solución debe poder ejecutarse localmente mediante contenedores y desplegarse en Kubernetes sin incluir datos de conexión ni credenciales en la imagen de la aplicación.

## Decisión

Se adopta una arquitectura de puertos y adaptadores con ejecución reactiva, persistencia PostgreSQL reactiva, mensajería Kafka y despliegue en Kubernetes.

### Aplicación

- El dominio y los casos de uso se mantienen independientes de los adaptadores de entrada y salida.
- Spring WebFlux y Reactor implementan la API HTTP no bloqueante.
- Spring Data R2DBC y el controlador R2DBC de PostgreSQL implementan la persistencia reactiva.
- Kafka publica los eventos de registro, cambio de estado y escalamiento de pacientes.
- Un planificador reactivo evalúa periódicamente los pacientes pendientes y registra los escalados en la auditoría.

### Contenedores

- La imagen se construye en dos etapas con Java 17: una etapa Maven de compilación y una imagen final JRE Alpine sin herramientas de compilación.
- La aplicación se ejecuta como un usuario no privilegiado y escucha en el puerto `8081`.
- Docker Compose proporciona PostgreSQL 17, Kafka 3.9 y el servicio para desarrollo local.

### Kubernetes

- Los recursos se despliegan en el namespace `vidacruz-prod`.
- El servicio de triage se ejecuta con dos réplicas detrás de un Service `ClusterIP`.
- PostgreSQL y Kafka se exponen solamente dentro del clúster mediante los servicios `postgres:5432` y `kafka:9092`.
- La API HTTP se publica mediante un Ingress que dirige el tráfico al Service `servicio-triage` en el puerto `8081`.
- Los parámetros de conexión se inyectan mediante variables de entorno. Las credenciales se obtienen de Secrets de Kubernetes, en vez de incluirse en la imagen o en `application.properties`.

## Configuración de conectividad

En Kubernetes, las variables del Deployment tienen prioridad sobre los valores locales de `application.properties`:

```text
SPRING_R2DBC_URL=r2dbc:postgresql://postgres:5432/triage_db
SPRING_R2DBC_USERNAME=<POSTGRES_USER desde postgres-secret>
SPRING_R2DBC_PASSWORD=<POSTGRES_PASSWORD desde postgres-secret>
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

Los nombres cortos `postgres` y `kafka` se resuelven mediante DNS interno de Kubernetes porque todos los recursos pertenecen al mismo namespace. Para acceder desde otro namespace se debe usar el nombre completo, por ejemplo `postgres.vidacruz-prod.svc.cluster.local`.

## Consecuencias

### Positivas

- La lógica de negocio puede probarse sin depender de HTTP, Kafka o PostgreSQL.
- El modelo reactivo evita bloquear hilos de servidor durante las operaciones de persistencia y mensajería.
- Los eventos Kafka desacoplan al servicio de los consumidores posteriores.
- La configuración por entorno permite usar `localhost` en desarrollo y DNS de Kubernetes en el clúster sin reconstruir la imagen.
- El Ingress publica únicamente la API HTTP; PostgreSQL y Kafka no quedan expuestos a Internet.

### Costos y riesgos

- El equipo debe operar y monitorizar Kafka, PostgreSQL, el controlador de Ingress y sus Secrets.
- La consistencia entre la escritura en PostgreSQL y la publicación en Kafka debe manejarse explícitamente; no existe una transacción distribuida entre ambos recursos.
- Los manifiestos actuales de PostgreSQL y Kafka usan `Deployment` de una sola réplica y no definen almacenamiento persistente. Son adecuados como base para desarrollo o pruebas, pero un ambiente productivo debe usar servicios gestionados o StatefulSets con PersistentVolumeClaims, copias de seguridad y una configuración de alta disponibilidad.
- Los valores de ejemplo `CAMBIAR_EN_PRODUCCION` y `REEMPLAZAR_POR_UUID_KAFKA_VALIDO` deben sustituirse antes de aplicar los manifiestos.

## Alternativas consideradas

### Aplicación bloqueante con Spring MVC y JDBC

Se descarta porque el servicio ya usa operaciones reactivas y necesita mantener un modelo consistente desde HTTP hasta la persistencia y Kafka.

### Comunicación síncrona entre servicios

Se descarta como mecanismo principal para notificar eventos de triage, ya que Kafka permite desacoplar a los consumidores y absorber picos de carga.

### Direcciones IP o localhost en la imagen

Se descarta porque impide que el mismo artefacto funcione correctamente en Docker Compose y Kubernetes. Los nombres de Service y las variables de entorno resuelven el destino según el ambiente.
