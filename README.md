# Microservicio de Gestión de Reservas de Salas

## Descripción
Microservicio Spring Boot para administrar el préstamo de salas de estudio, con validaciones de horarios y conflictos de reservas.

## Tecnologías
- Spring Boot 3.2.5
- Java 17
- Spring Data JPA
- H2 Database (en memoria)
- Lombok
- Spring Validation
- Maven

## Estructura del Proyecto
```
src/
  main/
    java/
      com/duoc/reservas/
        controller/     # Endpoints REST
        service/        # Lógica de negocio
        repository/     # Acceso a datos
        model/          # Entidades JPA
        exception/      # Manejo de excepciones
    resources/
      application.properties  # Configuración
```

## Endpoints API

### CRUD de Reservas
- `POST /api/reservas` - Crear nueva reserva
- `GET /api/reservas` - Listar todas las reservas
- `GET /api/reservas/{id}` - Buscar reserva por ID
- `PUT /api/reservas/{id}` - Actualizar reserva
- `DELETE /api/reservas/{id}` - Eliminar reserva
- `GET /api/reservas/sala/{sala}` - Listar reservas por sala

### Endpoint Adicional
- `GET /api/reservas/salas-disponibles?fecha=YYYY-MM-DD&horaInicio=HH:mm&horaTermino=HH:mm` - Consultar salas disponibles

## Datos de Reserva
```json
{
  "nombreEstudiante": "Juan Pérez",
  "correoEstudiante": "juan.perez@duoc.cl",
  "sala": "SALA-A",
  "fecha": "2026-04-15",
  "horaInicio": "10:00",
  "horaTermino": "12:00",
  "estadoReserva": "ACTIVA"
}
```

## Estados de Reserva
- `ACTIVA` - Reserva vigente
- `CANCELADA` - Reserva cancelada
- `FINALIZADA` - Reserva completada

## Validaciones Implementadas
- Correo electrónico válido
- Hora de término posterior a hora de inicio
- No permitir reservas en horarios ocupados
- Fecha no puede ser anterior a hoy
- Campos obligatorios

## Ejecutar la Aplicación

### Usando Maven
```bash
mvn spring-boot:run
```

### Compilar y Ejecutar
```bash
mvn clean package
java -jar target/reservas-salas-1.0.0.jar
```

## Base de Datos
- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:reservasdb`
- **Usuario**: `sa`
- **Contraseña**: `password`

## Ejemplos de Uso

### Crear Reserva
```bash
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "nombreEstudiante": "Ana García",
    "correoEstudiante": "ana.garcia@duoc.cl",
    "sala": "SALA-B",
    "fecha": "2026-04-16",
    "horaInicio": "14:00",
    "horaTermino": "16:00",
    "estadoReserva": "ACTIVA"
  }'
```

### Consultar Salas Disponibles
```bash
curl "http://localhost:8080/api/reservas/salas-disponibles?fecha=2026-04-16&horaInicio=14:00&horaTermino=16:00"
```

### Listar Reservas por Sala
```bash
curl http://localhost:8080/api/reservas/sala/SALA-A
```

## Manejo de Errores
La API devuelve respuestas estructuradas para diferentes tipos de errores:
- `400 BAD_REQUEST` - Errores de validación
- `404 NOT_FOUND` - Recurso no encontrado
- `409 CONFLICT` - Conflictos de horario
- `500 INTERNAL_SERVER_ERROR` - Errores del servidor

## Pruebas
Para ejecutar las pruebas unitarias:
```bash
mvn test
```

## Configuración Adicional
- **Puerto**: 8080 (configurable en `application.properties`)
- **CORS**: Habilitado para todos los orígenes
- **Logging**: Nivel DEBUG para el paquete com.duoc.reservas
