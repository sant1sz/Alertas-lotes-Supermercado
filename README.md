# AlertaLote

API REST para **minimercados y tiendas de barrio** que controla el inventario por **lotes** y avisa qué productos están **vencidos o próximos a vencer**, para retirarlos o rotarlos a tiempo y evitar pérdidas.

## Qué hace

- Registra **negocios**, **productos** (por código de barras) y **lotes** con cantidad, fecha de vencimiento y ubicación.
- Descuenta unidades de un lote cuando se vende o se retira.
- Lista los lotes **vencidos** y los **próximos a vencer**, filtrando por negocio.
- **Revisa automáticamente todos los días** (6:00 a. m.) y genera alertas sin repetirlas. Si un lote "próximo" ya venció, su alerta cambia a "vencido".
- Arma un **mensaje de WhatsApp** con las alertas pendientes y el enlace `wa.me` para enviarlo con un clic.
- Ordena los lotes por urgencia con una **cola de prioridad implementada desde cero** (montículo binario mínimo): sale primero el que vence antes y, si dos vencen el mismo día, el que tiene más unidades.

## Tecnologías

Java 21 · Spring Boot 4 (Web MVC, Data JPA, Validation, Scheduling) · MySQL · Maven · JUnit 5

## Arquitectura

```
controller/   Endpoints REST
service/      Reglas de negocio (validaciones, alertas, reporte)
repository/   Acceso a datos con Spring Data JPA
model/        Entidades: Negocio, Producto, Lote, Alerta
dto/          Datos de entrada y salida, con validaciones
estructura/   MonticuloMinimo y ColaVencimientos (cola de prioridad)
exception/    Manejo centralizado de errores (400, 404, 409)
config/       Reloj de la aplicación (zona horaria de Colombia)
```

## Cómo ejecutarlo

1. Ten MySQL corriendo en `localhost:3306`. La base de datos `alerta_lote_db` se crea sola si no existe.
2. Configura las credenciales de una de estas dos formas:
   - **Variables de entorno:** `DB_USER` y `DB_PASSWORD` (y `DB_URL` si tu servidor es otro).
   - **Archivo local:** crea `src/main/resources/application-local.properties` con
     ```properties
     spring.datasource.username=root
     spring.datasource.password=tu_contraseña
     ```
     Este archivo está en `.gitignore`, así que tu contraseña nunca se sube a GitHub.
3. Ejecuta:
   ```bash
   ./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
   ```
4. La API queda en `http://localhost:8080/api`. En `pruebas.http` hay ejemplos de todas las peticiones.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET / POST | `/api/negocios` | Listar / crear negocios |
| GET / PUT / DELETE | `/api/negocios/{id}` | Ver / actualizar / eliminar un negocio |
| GET | `/api/negocios/{id}/reporte?dias=7` | Vencidos y próximos a vencer, ordenados por urgencia |
| GET / POST | `/api/productos?negocio={id}` | Listar (opcionalmente por negocio) / crear productos |
| GET / PUT / DELETE | `/api/productos/{codigoBarras}` | Ver / actualizar / eliminar un producto |
| GET | `/api/productos/{codigoBarras}/lotes` | Lotes de un producto |
| GET / POST | `/api/lotes?negocio={id}` | Listar / registrar lotes |
| GET / PUT / DELETE | `/api/lotes/{id}` | Ver / actualizar / eliminar un lote |
| POST | `/api/lotes/{id}/retiros` | Descontar unidades (venta o retiro) |
| GET | `/api/lotes/proximos-a-vencer?dias=7&negocio={id}` | Lotes que vencen en los próximos N días |
| GET | `/api/lotes/vencidos?negocio={id}` | Lotes vencidos con unidades |
| GET | `/api/alertas?negocio={id}` | Alertas pendientes |
| POST | `/api/alertas/generar` | Ejecutar la revisión ahora |
| PATCH | `/api/alertas/{id}/atendida` | Marcar una alerta como atendida |
| GET | `/api/alertas/whatsapp/{idNegocio}` | Mensaje y enlace de WhatsApp con las alertas |

### Ejemplo: registrar un lote

```http
POST /api/lotes
Content-Type: application/json

{
  "codigoBarras": "7702001000011",
  "cantidad": 15,
  "fechaVencimiento": "2026-11-12",
  "ubicacion": "Nevera 2"
}
```

### Ejemplo de error

```json
{
  "estado": 400,
  "error": "Bad Request",
  "mensaje": "Hay datos inválidos en la petición",
  "detalles": { "cantidad": "La cantidad no puede ser negativa" }
}
```

## Configuración de alertas

En `application.properties`:

```properties
alertas.dias-anticipacion=7      # con cuántos días de anticipación avisar
alertas.cron=0 0 6 * * *         # todos los días a las 6:00 a. m.
alertas.zona-horaria=America/Bogota
```

## Pruebas

```bash
./mvnw test
```

Incluye pruebas del montículo (orden correcto con 1.000 datos aleatorios), de la cola de vencimientos y de la redacción de alertas. La prueba `contextLoads` necesita MySQL encendido.

## Próximos pasos

- Envío automático por la API oficial de WhatsApp Business.
- Autenticación para que cada negocio vea solo sus datos.
- Interfaz web o móvil para escanear el código de barras al registrar un lote.

## Autor

**Santiago Sánchez Ramírez**: estudiante de Ingeniería de Sistemas en el ITM, Medellín.
[GitHub](https://github.com/sant1sz) · [LinkedIn](https://www.linkedin.com/in/santiago-sanchez-ramirez)
