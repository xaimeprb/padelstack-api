# PadelStack API

Backend Spring Boot para la app Android de PadelStack.

## Qué incluye

- Prefijo `/api/v1`
- Validación de `Authorization: Bearer <Firebase ID token>`
- Verificación del token con Firebase Admin SDK
- Identidad real obtenida del token + `users/{uid}`
- Firestore como almacenamiento principal
- Endpoints obligatorios de:
  - bootstrap de usuario
  - perfil `me`
  - recursos
  - disponibilidad
  - reservas
  - incidencias
  - anuncios
  - estatutos
  - administración básica
- Reglas e índices de Firebase
- `multipart/form-data` para incidencias con foto

## Requisitos

- Java 17+
- Maven 3.9+
- Proyecto Firebase con:
  - Authentication
  - Firestore
  - Storage
- Service account JSON

## Configuración

Variables recomendadas:

```bash
export FIREBASE_CREDENTIALS_PATH=/ruta/service-account.json
export FIREBASE_PROJECT_ID=tu-proyecto-firebase
export FIREBASE_STORAGE_BUCKET=tu-proyecto.firebasestorage.app
export APP_PUBLIC_BASE_URL=http://10.0.2.2:8080
```

También puedes usar `GOOGLE_APPLICATION_CREDENTIALS` si prefieres Application Default Credentials.

## Arranque

```bash
mvn spring-boot:run
```

o

```bash
mvn clean package
java -jar target/padelstack-api-1.0.0.jar
```

En Windows, desde `Codigo/padelstack-api`, puedes arrancarla con las
variables locales ya preparadas:

```powershell
powershell -ExecutionPolicy Bypass -File .\start-local-api.ps1 -Build
```

La API quedara en `http://localhost:8080`. Desde el emulador Android se
accede como `http://10.0.2.2:8080`.

## Colecciones Firestore esperadas

- `communities/{communityId}`
- `users/{uid}`
- `resources/{resourceId}`
- `reservations/{reservationId}`
- `incidents/{incidentId}`
- `announcements/{announcementId}`
- `statutes/{communityId}`
- `audit_logs/{logId}`

## Seed mínimo recomendado

### Comunidad

`communities/montesauce`

```json
{
  "communityId": "montesauce",
  "name": "Montesauce",
  "active": true,
  "units": ["2-1-A", "2-1-B", "2-2-A", "2-2-B"]
}
```

### Recursos

`resources/PADEL_1`

```json
{
  "resourceId": "PADEL_1",
  "communityId": "montesauce",
  "name": "Pista de pádel",
  "type": "PADEL",
  "reservationMode": "SLOT",
  "slotMinutes": 90,
  "openTime": "09:00",
  "closeTime": "21:00",
  "rulesText": "Máximo una reserva activa por tramo",
  "active": true
}
```

`resources/MERENDERO_1`

```json
{
  "resourceId": "MERENDERO_1",
  "communityId": "montesauce",
  "name": "Merendero",
  "type": "MERENDERO",
  "reservationMode": "FULL_DAY",
  "slotMinutes": null,
  "openTime": null,
  "closeTime": null,
  "rulesText": "Reserva por día completo",
  "active": true
}
```

### Estatutos

`statutes/montesauce`

```json
{
  "communityId": "montesauce",
  "title": "Normativa de la comunidad",
  "content": "Texto completo...",
  "version": 1,
  "updatedAt": "2026-04-03T10:00:00Z",
  "updatedByUid": "firebase_uid"
}
```

## Notas importantes

- El backend **ignora** `uid`, `email`, `role`, `communityId`, `createdBy` enviados por cliente cuando no proceden.
- En `bootstrap`, el `communityName` del body no se confía: se resuelve desde Firestore.
- `401` devuelve `{ "message": "Unauthorized" }`
- `403` devuelve `{ "message": "No tienes permisos" }`
- `409` devuelve `{ "message": "La reserva ya no está disponible" }`
- `400` devuelve `{ "message": "Datos inválidos" }`

## Endpoints extra útiles

Además de los obligatorios, se deja preparado:

- `PUT /api/v1/admin/incidents/{incidentId}/status`
- `GET /api/v1/public/incidents/{incidentId}/photo?path=...`

## Limitaciones honestas

- No incluye migraciones automáticas ni seed runner.
- No he metido Swagger porque ahora mismo no te aporta más que peso.
- Las fotos se sirven a través del backend para que `photoUrl` sea usable desde Android sin abrir reglas cliente.
