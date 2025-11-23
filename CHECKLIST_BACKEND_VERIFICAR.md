# ✅ CHECKLIST VERIFICACIÓN BACKEND

## CAMPOS CRÍTICOS QUE DEBE RETORNAR GET /api/owners/me/citas/pendientes

### ❌ FALTA SI NO RETORNA:
- [ ] duenioNombre
- [ ] nombreMascota
- [ ] duenioTelefono
- [ ] duenioCorreo
- [ ] fechaIso
- [ ] motivo
- [ ] mascotaId
- [ ] estado

---

## CAMPOS CRÍTICOS QUE DEBE RETORNAR GET /api/owners/me/citas/completadas

### ❌ FALTA SI NO RETORNA:
- [ ] duenioNombre
- [ ] nombreMascota
- [ ] duenioTelefono
- [ ] duenioCorreo
- [ ] horaInicio (formato HH:mm)
- [ ] horaFin (formato HH:mm)
- [ ] diagnostico
- [ ] procedimientos
- [ ] recomendaciones
- [ ] veterinarioNombre
- [ ] fechaIso
- [ ] motivo
- [ ] mascotaId
- [ ] estado

---

## VALIDACIONES ENDPOINT PUT /api/owners/me/citas/{id}

### ❌ DEBE VALIDAR:
- [ ] Formato horaInicio sea HH:mm
- [ ] Formato horaFin sea HH:mm
- [ ] horaFin >= horaInicio
- [ ] Solo si estado = "completada"
- [ ] Solo si user es veterinario que la atendió

### ❌ DEBE RETORNAR:
- [ ] Cita completa con TODOS los campos
- [ ] Status 200 si ok
- [ ] Status 400 si validación falla
- [ ] Status 401 si no autorizado
- [ ] Status 403 si sin permiso

---

## VERIFICA EN TU BACKEND:

1. En controlador de Citas:
   - ¿GET /api/owners/me/citas/pendientes retorna duenioNombre?
   - ¿GET /api/owners/me/citas/completadas retorna horaInicio y horaFin?
   - ¿PUT /api/owners/me/citas/{id} acepta horaInicio y horaFin?

2. En modelo Cita:
   - ¿Tiene campo horaInicio?
   - ¿Tiene campo horaFin?
   - ¿Tiene validación de formato HH:mm?

3. En servicio/lógica:
   - ¿Valida que horaFin >= horaInicio?
   - ¿Retorna duenioNombre (no ID)?
   - ¿Retorna nombreMascota (no ID)?

