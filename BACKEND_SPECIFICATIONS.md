# 🔌 Especificación de Endpoints del Backend

## 📌 Endpoints Requeridos para el Sistema de Citas Mejorado

### 1. ENDPOINTS PARA DUEÑOS (Owner)

#### 1.1 Obtener todas mis citas
```
GET /api/owners/me/citas
```

**Respuesta:**
```json
[
  {
    "id": "cita-001",
    "fechaIso": "2025-01-20T10:30:00",
    "motivo": "Revisión general",
    "mascotaId": "mascota-123",
    "estado": "pendiente",
    "notas": null,
    "veterinarioNombre": null,
    "veterinarioId": null,
    "diagnostico": null,
    "procedimientos": null,
    "recomendaciones": null,
    "horaInicio": null,
    "horaFin": null,
    "duenioTelefono": "+34 666 555 444",
    "duenioCorreo": "owner@example.com"
  }
]
```

---

#### 1.2 Obtener mis citas PENDIENTES
```
GET /api/owners/me/citas/pendientes
```

**Respuesta:** (Igual al endpoint anterior, pero solo citas con `estado != "completada"`)

---

#### 1.3 Obtener mis citas COMPLETADAS
```
GET /api/owners/me/citas/completadas
```

**Respuesta:** (Igual al endpoint anterior, pero solo citas con `estado == "completada"`)

```json
[
  {
    "id": "cita-002",
    "fechaIso": "2025-01-15T10:30:00",
    "motivo": "Revisión general",
    "mascotaId": "mascota-123",
    "estado": "completada",
    "notas": "Todo fue bien",
    "veterinarioNombre": "Dr. García",
    "veterinarioId": "vet-001",
    "diagnostico": "Gato saludable",
    "procedimientos": "Examen físico completo, análisis de sangre",
    "recomendaciones": "Mantener vacunas al día, revisión en 6 meses",
    "horaInicio": "10:30",
    "horaFin": "11:15",
    "duenioTelefono": "+34 666 555 444",
    "duenioCorreo": "owner@example.com"
  }
]
```

---

### 2. ENDPOINTS PARA VETERINARIOS (Vet)

#### 2.1 Obtener todas las citas (solo veterinarios)
```
GET /api/vet/citas
```

**Respuesta:**
```json
[
  {
    "id": "cita-001",
    "fechaIso": "2025-01-20T10:30:00",
    "fecha": "2025-01-20",
    "motivo": "Revisión general",
    "mascotaId": "mascota-123",
    "ownerId": "owner-456",
    "estado": "pendiente",
    "mascotaNombre": "Rex",
    "duenioNombre": "Juan Pérez",
    "mascotaTelefono": null,
    "duenioTelefono": "+34 666 555 444",
    "duenioCorreo": "juan@example.com",
    "notas": null,
    "diagnostico": null,
    "procedimientos": null,
    "recomendaciones": null,
    "horaInicio": null,
    "horaFin": null
  }
]
```

---

#### 2.2 Actualizar cita (con ficha médica)
```
PATCH /api/vet/citas/{id}
```

**Body de Request:**
```json
{
  "estado": "completada",
  "notas": "Atención exitosa",
  "diagnostico": "Gato sano, sin problemas aparentes",
  "procedimientos": "Examen físico completo, vacunación antirrábica",
  "recomendaciones": "Mantener dieta balanceada, próxima revisión en 6 meses",
  "horaInicio": "10:30",
  "horaFin": "11:15"
}
```

**Campos Opcionales:**
- `estado`: "pendiente", "en_curso", "completada"
- `notas`: Observaciones generales
- `diagnostico`: Diagnóstico médico
- `procedimientos`: Procedimientos realizados
- `recomendaciones`: Recomendaciones al dueño
- `horaInicio`: Hora formato HH:mm
- `horaFin`: Hora formato HH:mm

**Respuesta:** (Cita actualizada con los nuevos valores)

---

### 3. ESTRUCTURA COMPLETA DE CitaDto

```json
{
  "id": "cita-001",
  "fechaIso": "2025-01-20T10:30:00Z",
  "fecha": "2025-01-20",
  "motivo": "Revisión general",
  "mascotaId": "mascota-123",
  "ownerId": "owner-456",
  "estado": "completada",
  "mascotaNombre": "Rex",
  "duenioNombre": "Juan Pérez",
  "mascotaTelefono": null,
  "duenioTelefono": "+34 666 555 444",
  "duenioCorreo": "juan@example.com",
  "notas": "Atención sin complicaciones",
  "diagnostico": "Saludable",
  "procedimientos": "Examen físico, vacunación",
  "recomendaciones": "Revisar en 6 meses",
  "horaInicio": "10:30",
  "horaFin": "11:15",
  "veterinarioNombre": "Dr. García",
  "veterinarioId": "vet-001"
}
```

---

## 📊 Valores Permitidos

### Estados de Cita
```
"pendiente"    - Cita programada pero no iniciada
"en_curso"     - Cita en progreso
"completada"   - Cita finalizada
```

### Formatos de Fecha y Hora
```
fechaIso: ISO 8601 format → "2025-01-20T10:30:00Z"
fecha: Formato simple → "2025-01-20"
horaInicio: HH:mm → "10:30"
horaFin: HH:mm → "11:15"
```

---

## 🔐 Autenticación

Todos los endpoints requieren token Bearer en el header:
```
Authorization: Bearer {token}
```

---

## ✅ Implementación Recomendada en el Backend

### Base de datos
```sql
CREATE TABLE citas (
    id VARCHAR(36) PRIMARY KEY,
    fecha_iso DATETIME NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    mascota_id VARCHAR(36) NOT NULL,
    owner_id VARCHAR(36) NOT NULL,
    veterinario_id VARCHAR(36),
    estado ENUM('pendiente', 'en_curso', 'completada') DEFAULT 'pendiente',
    notas TEXT,
    diagnostico TEXT,
    procedimientos TEXT,
    recomendaciones TEXT,
    hora_inicio TIME,
    hora_fin TIME,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (mascota_id) REFERENCES mascotas(id),
    FOREIGN KEY (owner_id) REFERENCES owners(id),
    FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id)
);
```

### Ejemplo con Spring Boot
```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CitasController {

    private final CitasService citasService;
    private final SecurityUtils securityUtils;

    @GetMapping("/owners/me/citas/pendientes")
    public ResponseEntity<List<CitaDTO>> getMyCitasPendientes() {
        String ownerId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(citasService.getCitasPendientes(ownerId));
    }

    @GetMapping("/owners/me/citas/completadas")
    public ResponseEntity<List<CitaDTO>> getMyCitasCompletadas() {
        String ownerId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(citasService.getCitasCompletadas(ownerId));
    }

    @PatchMapping("/vet/citas/{id}")
    public ResponseEntity<CitaDTO> updateCita(
            @PathVariable String id,
            @RequestBody CitaUpdateRequest request) {
        String vetId = securityUtils.getCurrentUserId();
        CitaDTO updated = citasService.updateCita(id, request, vetId);
        return ResponseEntity.ok(updated);
    }
}
```

---

## 🧪 Casos de Prueba

### Test 1: Obtener citas pendientes del dueño
- GET `/api/owners/me/citas/pendientes`
- Debe devolver solo citas con `estado != "completada"`

### Test 2: Obtener citas completadas del dueño
- GET `/api/owners/me/citas/completadas`
- Debe devolver solo citas con `estado == "completada"`
- Debe incluir: `diagnostico`, `procedimientos`, `recomendaciones`, `horaInicio`, `horaFin`

### Test 3: Veterinario actualiza cita
- PATCH `/api/vet/citas/{id}`
- Body: `{ "estado": "completada", "diagnostico": "...", ... }`
- Respuesta: Cita actualizada con todos los campos

### Test 4: Campos de contacto del dueño
- GET `/api/owners/me/citas/pendientes`
- Respuesta debe incluir: `duenioTelefono`, `duenioCorreo`

---

## 🐛 Manejo de Errores

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "El estado debe ser uno de: pendiente, en_curso, completada"
}
```

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Token inválido o expirado"
}
```

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Solo veterinarios pueden actualizar citas"
}
```

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Cita con id 'cita-001' no encontrada"
}
```

---

## 📝 Notas Importantes

1. **Tokens**: Asegurar que el endpoint verifica que solo el propietario/veterinario pueda acceder a sus datos
2. **Validaciones**: Validar que los campos requeridos no sean null
3. **Timestamps**: Incluir `created_at` y `updated_at` para auditoría
4. **Filtrado**: Los endpoints de "pendientes" y "completadas" hacen filtrado en el servidor
5. **Campos null**: Los campos opcionales como `diagnostico` pueden ser null en citas pendientes

