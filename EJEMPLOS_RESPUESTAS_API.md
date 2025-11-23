
---

## 4️⃣ Errores que Android Espera Manejar

### Error 400 - Validación fallida

```json
{
  "status": 400,
  "message": "Validación fallida",
  "errors": [
    {
      "field": "horaFin",
      "message": "horaFin debe ser >= horaInicio"
    }
  ]
}
```
**Android muestra:** Toast "Error: horaFin debe ser >= horaInicio"

---

### Error 401 - No autorizado

```json
{
  "status": 401,
  "message": "Token expirado o inválido"
}
```
**Android muestra:** Toast "Sesión expirada. Inicia sesión nuevamente."

---

### Error 403 - Forbidden

```json
{
  "status": 403,
  "message": "No tienes permiso para actualizar esta cita"
}
```
**Android muestra:** Toast "Error: No tienes permiso"

---

### Error 404 - No encontrado

```json
{
  "status": 404,
  "message": "Cita no encontrada"
}
```
**Android muestra:** Toast "Error: Cita no encontrada"

---

### Error 500 - Error del servidor

```json
{
  "status": 500,
  "message": "Error interno del servidor"
}
```
**Android muestra:** Toast "Error: Error interno del servidor"

---

## 📋 VALIDACIONES QUE BACKEND DEBE HACER

### En PUT `/api/owners/me/citas/{id}`

```javascript
// 1. Validar que el usuario esté autenticado
if (!req.user) {
  return res.status(401).json({ error: 'No autorizado' });
}

// 2. Validar que la cita exista
const cita = await Cita.findById(id);
if (!cita) {
  return res.status(404).json({ error: 'Cita no encontrada' });
}

// 3. Validar que la cita esté completada
if (cita.estado !== 'completada') {
  return res.status(400).json({ error: 'Solo se pueden actualizar citas completadas' });
}

// 4. Validar que el veterinario sea quien la atendió
if (cita.veterinarioId !== req.user.id && req.user.role !== 'admin') {
  return res.status(403).json({ error: 'No tienes permiso' });
}

// 5. Validar formato de horaInicio si se proporciona
if (horaInicio && !/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/.test(horaInicio)) {
  return res.status(400).json({ error: 'Formato inválido para horaInicio. Use HH:mm' });
}

// 6. Validar formato de horaFin si se proporciona
if (horaFin && !/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/.test(horaFin)) {
  return res.status(400).json({ error: 'Formato inválido para horaFin. Use HH:mm' });
}

// 7. Validar que horaFin >= horaInicio
if (horaInicio && horaFin) {
  const [hI, mI] = horaInicio.split(':').map(Number);
  const [hF, mF] = horaFin.split(':').map(Number);
  const minI = hI * 60 + mI;
  const minF = hF * 60 + mF;
  
  if (minF < minI) {
    return res.status(400).json({ error: 'horaFin debe ser >= horaInicio' });
  }
}

// 8. Actualizar cita
cita.horaInicio = horaInicio || cita.horaInicio;
cita.horaFin = horaFin || cita.horaFin;
await cita.save();

// 9. Retornar cita completa con TODOS los campos
return res.json({
  id: cita._id,
  fechaIso: cita.fechaIso,
  motivo: cita.motivo,
  mascotaId: cita.mascotaId,
  estado: cita.estado,
  horaInicio: cita.horaInicio,
  horaFin: cita.horaFin,
  diagnostico: cita.diagnostico,
  procedimientos: cita.procedimientos,
  recomendaciones: cita.recomendaciones,
  veterinarioNombre: vet.nombre,
  duenioNombre: owner.nombre,
  nombreMascota: mascota.nombre,
  duenioTelefono: owner.telefono,
  duenioCorreo: owner.email
});
```

---

## ✅ CHECKLIST DE IMPLEMENTACIÓN BACKEND

```
[ ] GET /api/owners/me/citas/pendientes
    [ ] Retorna lista de CitaDto
    [ ] Incluye duenioNombre
    [ ] Incluye nombreMascota
    [ ] Incluye duenioTelefono
    [ ] Incluye duenioCorreo
    [ ] Retorna null para horaInicio/horaFin si no existen

[ ] GET /api/owners/me/citas/completadas
    [ ] Retorna lista de CitaDto
    [ ] Incluye TODOS los campos (ver punto anterior)
    [ ] Incluye horaInicio y horaFin
    [ ] Incluye diagnostico, procedimientos, recomendaciones
    [ ] Incluye veterinarioNombre

[ ] PUT /api/owners/me/citas/{id}
    [ ] Acepta horaInicio en formato HH:mm
    [ ] Acepta horaFin en formato HH:mm
    [ ] Valida que sea formato 24 horas
    [ ] Valida que horaFin >= horaInicio
    [ ] Valida que solo se actualice si estado === "completada"
    [ ] Valida que solo el veterinario o admin pueda actualizar
    [ ] Retorna cita completa actualizada
    [ ] Retorna errores con status codes correctos (400, 401, 403, 404)
```

---

## 🎯 TESTING MANUAL CON POSTMAN/CURL

### Test 1: Obtener citas pendientes
```bash
curl -X GET \
  -H "Authorization: Bearer TOKEN_JWT" \
  http://localhost:3000/api/owners/me/citas/pendientes \
  | jq '.'
```

**Verificar:**
- ✅ Status 200
- ✅ Array no vacío (o array vacío si no hay)
- ✅ Cada objeto tiene duenioNombre
- ✅ Cada objeto tiene nombreMascota

### Test 2: Obtener citas completadas
```bash
curl -X GET \
  -H "Authorization: Bearer TOKEN_JWT" \
  http://localhost:3000/api/owners/me/citas/completadas \
  | jq '.'
```

**Verificar:**
- ✅ Status 200
- ✅ Array con citas completadas
- ✅ Cada objeto tiene horaInicio y horaFin
- ✅ Cada objeto tiene diagnostico, procedimientos, recomendaciones

### Test 3: Actualizar cita con horas válidas
```bash
curl -X PUT \
  -H "Authorization: Bearer TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{"horaInicio": "14:30", "horaFin": "15:00"}' \
  http://localhost:3000/api/owners/me/citas/CITA_ID \
  | jq '.'
```

**Verificar:**
- ✅ Status 200
- ✅ Retorna objeto cita completo
- ✅ horaInicio = "14:30"
- ✅ horaFin = "15:00"

### Test 4: Actualizar cita con horas inválidas (horaFin < horaInicio)
```bash
curl -X PUT \
  -H "Authorization: Bearer TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{"horaInicio": "15:30", "horaFin": "14:00"}' \
  http://localhost:3000/api/owners/me/citas/CITA_ID \
  | jq '.'
```

**Verificar:**
- ✅ Status 400
- ✅ Message: "horaFin debe ser >= horaInicio"

---

**Si todo esto está implementado en el backend, Android funcionará perfectamente! 🚀**
# 📡 EJEMPLOS EXACTOS DE RESPUESTAS QUE ANDROID ESPERA

## 1️⃣ GET `/api/owners/me/citas/pendientes` - Respuesta Esperada

```json
{
  "status": 200,
  "data": [
    {
      "id": "cita-uuid-001",
      "fechaIso": "2025-01-25 10:00",
      "motivo": "Revisión general",
      "mascotaId": "mascota-uuid-001",
      "estado": "pendiente",
      "duenioNombre": "Carlos Mendez",
      "nombreMascota": "Firulais",
      "duenioTelefono": "+5491234567",
      "duenioCorreo": "carlos@email.com",
      "veterinarioNombre": null,
      "veterinarioId": null,
      "horaInicio": null,
      "horaFin": null,
      "diagnostico": null,
      "procedimientos": null,
      "recomendaciones": null
    },
    {
      "id": "cita-uuid-002",
      "fechaIso": "2025-01-26 14:30",
      "motivo": "Vacunación",
      "mascotaId": "mascota-uuid-002",
      "estado": "pendiente",
      "duenioNombre": "Ana Rodriguez",
      "nombreMascota": "Michi",
      "duenioTelefono": "+5499876543",
      "duenioCorreo": "ana@email.com",
      "veterinarioNombre": null,
      "veterinarioId": null,
      "horaInicio": null,
      "horaFin": null,
      "diagnostico": null,
      "procedimientos": null,
      "recomendaciones": null
    }
  ]
}
```

### ¿Qué ve el usuario en Android?

**Tab: Pendientes (2)**
```
┌─────────────────────────────────────┐
│ Tutor: Carlos Mendez    [PENDIENTE]│
│ Revisión general                    │
├─────────────────────────────────────┤
│ 📅 2025-01-25 10:00                 │
│ 🐾 Mascota: Firulais                │
│ ☎️ Tel: +5491234567                │
│ ✉️ Email: carlos@email.com          │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Tutor: Ana Rodriguez    [PENDIENTE] │
│ Vacunación                          │
├─────────────────────────────────────┤
│ 📅 2025-01-26 14:30                 │
│ 🐾 Mascota: Michi                   │
│ ☎️ Tel: +5499876543                │
│ ✉️ Email: ana@email.com             │
└─────────────────────────────────────┘
```

---

## 2️⃣ GET `/api/owners/me/citas/completadas` - Respuesta Esperada

```json
{
  "status": 200,
  "data": [
    {
      "id": "cita-uuid-003",
      "fechaIso": "2025-01-20 15:30",
      "motivo": "Revisión general",
      "mascotaId": "mascota-uuid-001",
      "estado": "completada",
      "duenioNombre": "Carlos Mendez",
      "nombreMascota": "Firulais",
      "duenioTelefono": "+5491234567",
      "duenioCorreo": "carlos@email.com",
      "veterinarioNombre": "Dr. Juan García",
      "veterinarioId": "vet-uuid-001",
      "horaInicio": "15:30",
      "horaFin": "16:00",
      "diagnostico": "Perro en excelente estado de salud",
      "procedimientos": "Vacunación antirrábica, Desparasitación interna",
      "recomendaciones": "Mantener vacunas al día. Próxima revisión en 6 meses"
    },
    {
      "id": "cita-uuid-004",
      "fechaIso": "2025-01-19 10:00",
      "motivo": "Castración",
      "mascotaId": "mascota-uuid-002",
      "estado": "completada",
      "duenioNombre": "Ana Rodriguez",
      "nombreMascota": "Michi",
      "duenioTelefono": "+5499876543",
      "duenioCorreo": "ana@email.com",
      "veterinarioNombre": "Dra. María López",
      "veterinarioId": "vet-uuid-002",
      "horaInicio": "10:00",
      "horaFin": "11:30",
      "diagnostico": "Cirugía exitosa sin complicaciones",
      "procedimientos": "Ovariohisterectomía laparoscópica",
      "recomendaciones": "Reposo completo 10 días. Revisión de puntos al día 14"
    }
  ]
}
```

### ¿Qué ve el usuario en Android?

**Tab: Completadas (2)**
```
┌─────────────────────────────────────┐
│ Tutor: Carlos Mendez  [COMPLETADA] │
│ Atendido por: Dr. Juan García       │
├─────────────────────────────────────┤
│ 📅 2025-01-20 15:30                 │
│ 🕐 15:30 - 16:00 ✏️                 │
│ 🐾 Mascota: Firulais                │
│ ☎️ Tel: +5491234567                │
│ ✉️ Email: carlos@email.com          │
├─────────────────────────────────────┤
│ Ficha Médica                        │
│ ┌─────────────────────────────────┐ │
│ │ Diagnóstico                     │ │
│ │ Perro en excelente estado de    │ │
│ │ salud                           │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ Procedimientos                  │ │
│ │ Vacunación antirrábica,         │ │
│ │ Desparasitación interna         │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ Recomendaciones                 │ │
│ │ Mantener vacunas al día.        │ │
│ │ Próxima revisión en 6 meses     │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Tutor: Ana Rodriguez  [COMPLETADA]  │
│ Atendido por: Dra. María López      │
├─────────────────────────────────────┤
│ 📅 2025-01-19 10:00                 │
│ 🕐 10:00 - 11:30 ✏️                 │
│ 🐾 Mascota: Michi                   │
│ ☎️ Tel: +5499876543                │
│ ✉️ Email: ana@email.com             │
├─────────────────────────────────────┤
│ Ficha Médica                        │
│ [diagnóstico, procedimientos, etc]  │
└─────────────────────────────────────┘
```

---

## 3️⃣ PUT `/api/owners/me/citas/{id}` - Solicitud y Respuesta

### Solicitud (desde Android)

```http
PUT /api/owners/me/citas/cita-uuid-003
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "horaInicio": "15:30",
  "horaFin": "16:00"
}
```

### Respuesta Esperada (200 OK)

```json
{
  "status": 200,
  "message": "Cita actualizada exitosamente",
  "data": {
    "id": "cita-uuid-003",
    "fechaIso": "2025-01-20 15:30",
    "motivo": "Revisión general",
    "mascotaId": "mascota-uuid-001",
    "estado": "completada",
    "duenioNombre": "Carlos Mendez",
    "nombreMascota": "Firulais",
    "duenioTelefono": "+5491234567",
    "duenioCorreo": "carlos@email.com",
    "veterinarioNombre": "Dr. Juan García",
    "veterinarioId": "vet-uuid-001",
    "horaInicio": "15:30",
    "horaFin": "16:00",
    "diagnostico": "Perro en excelente estado de salud",
    "procedimientos": "Vacunación antirrábica, Desparasitación interna",
    "recomendaciones": "Mantener vacunas al día. Próxima revisión en 6 meses"
  }
}
```

### Lo que Android hace con la respuesta

1. Cierra el selector de horas
2. Muestra Toast: "✓ Horario guardado"
3. Actualiza la tarjeta para mostrar: "🕐 15:30 - 16:00 ✏️"
4. Permite editar haciendo click en las horas

