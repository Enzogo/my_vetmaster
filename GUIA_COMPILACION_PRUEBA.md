# 🚀 Guía de Compilación y Prueba

## ✅ Compilación del Proyecto

### Opción 1: Compilación desde IDE (Android Studio)

1. Abre el proyecto en Android Studio
2. Sincroniza Gradle:
   - `File → Sync Now`
3. Compila el proyecto:
   - `Build → Build Project` (Ctrl+F9)
4. Espera a que termine la compilación

### Opción 2: Compilación desde Terminal

```bash
cd "C:\Users\enzog\OneDrive\Escritorio\My_Vet-master"
gradlew.bat compileDebugKotlin
```

---

## 🔍 Verificación de Archivos

### Archivos Creados:
- ✅ `/citas/HistorialCitasScreenNew.kt` (nueva pantalla de historial)
- ✅ `/CAMBIOS_CITAS.md` (resumen de cambios)
- ✅ `/BACKEND_SPECIFICATIONS.md` (especificaciones del backend)

### Archivos Modificados:
- ✅ `/network/VetApi.kt` (modelos expandidos)
- ✅ `/network/OwnerApi.kt` (modelos y endpoints)
- ✅ `/vet/VetCitasScreen.kt` (UI mejorada)
- ✅ `/MainScreen.kt` (integración de navegación)

---

## 🧪 Plan de Prueba

### Fase 1: Pruebas de UI (Sin Backend)

**Objetivo:** Verificar que la interfaz se renderiza correctamente

**Pasos:**
1. Instala la app en emulador/dispositivo
2. Navega a la pestaña "Historial"
3. Deberías ver:
   - ✅ Encabezado con ícono de historial
   - ✅ Dos tabs: "Pendientes" y "Completadas"
   - ✅ Mensaje "No hay citas pendientes/completadas"
4. Navega a "Citas" (Veterinario - si tienes ese role)
5. Deberías ver:
   - ✅ Tarjetas de citas
   - ✅ Botón "Ficha Médica" expandible
   - ✅ Campos para diagnóstico, procedimientos, recomendaciones

---

### Fase 2: Pruebas de Integración con Backend

**Requisitos:**
- Backend corriendo
- Endpoints implementados
- Autenticación funcionando

**Caso de Prueba 1: Dueño - Ver Historial**
1. Inicia sesión como dueño
2. Ve a "Historial"
3. Tab "Pendientes" debe mostrar citas con `estado != "completada"`
4. Tab "Completadas" debe mostrar citas con `estado == "completada"`
5. Cada tarjeta debe mostrar:
   - Información de la mascota
   - Motivo de la cita
   - Tutor (nombre, teléfono, email)
   - Para completadas: Veterinario, horas, diagnóstico

**Caso de Prueba 2: Veterinario - Actualizar Cita**
1. Inicia sesión como veterinario
2. Ve a "Citas"
3. Selecciona una cita pendiente
4. Cambia estado a "En curso"
5. Expande "Ficha Médica"
6. Llena campos: Diagnóstico, Procedimientos, Recomendaciones
7. Cambia estado a "Completada"
8. Aparecen campos de Hora Inicio y Hora Fin
9. Ingresa horas (ej: 10:30 - 11:00)
10. Presiona "Guardar Cambios"
11. Recibe confirmación "✓ Cita actualizada"

**Caso de Prueba 3: Dueño - Ver Cita Completada**
1. Vuelve a iniciar sesión como dueño
2. Ve a "Historial"
3. Tab "Completadas"
4. Selecciona una cita completada
5. Verifica que se muestren:
   - ✅ Datos del veterinario
   - ✅ Horas de atención
   - ✅ Ficha médica completa

---

## ⚠️ Posibles Errores y Soluciones

### Error 1: "Unresolved reference 'HistorialCitasScreen'"
**Causa:** El import está incorrecto o el archivo no existe
**Solución:**
- Verifica que `HistorialCitasScreenNew.kt` exista
- Limpia el proyecto: `Build → Clean Project`
- Sincroniza Gradle: `File → Sync Now`

### Error 2: "No parameter with name 'duenioTelefono' found"
**Causa:** CitaDto no tiene el campo
**Solución:**
- Verifica que `OwnerApi.kt` tenga `duenioTelefono: String?` en CitaDto
- Recompila: `Build → Rebuild Project`

### Error 3: "404 - Endpoint not found"
**Causa:** Backend no implementó los endpoints
**Solución:**
- Verifica que el backend tenga:
  - `GET /api/owners/me/citas/pendientes`
  - `GET /api/owners/me/citas/completadas`
  - `PATCH /api/vet/citas/{id}`
- Consulta `BACKEND_SPECIFICATIONS.md`

### Error 4: "Argument type mismatch"
**Causa:** Un parámetro tiene tipo incorrecto
**Solución:**
- Ejecuta `gradlew.bat compileDebugKotlin` para ver el error exacto
- Verifica los tipos de datos en los modelos

### Error 5: "Empty state" en historial
**Causa:** Los endpoints devuelven lista vacía o error
**Solución:**
- Verifica logs con Android Studio Logcat
- Busca mensajes de error HTTP (401, 403, 404)
- Verifica que tengas citas en la base de datos

---

## 📱 Simulación de Datos (Para Testing Sin Backend)

Si quieres testear la UI sin backend, puedes modificar temporalmente `HistorialCitasScreen`:

```kotlin
// Agregar esto en loadCitas() para pruebas:
citasPendientes = listOf(
    CitaDto(
        id = "1",
        fechaIso = "2025-01-20T10:30:00",
        motivo = "Revisión general",
        mascotaId = "mascota-1",
        duenioTelefono = "+34 666 555 444",
        duenioCorreo = "test@example.com",
        estado = "pendiente"
    )
)

citasCompletadas = listOf(
    CitaDto(
        id = "2",
        fechaIso = "2025-01-15T10:30:00",
        motivo = "Vacunación",
        mascotaId = "mascota-2",
        duenioTelefono = "+34 666 555 444",
        duenioCorreo = "test@example.com",
        estado = "completada",
        veterinarioNombre = "Dr. García",
        diagnostico = "Todo bien",
        procedimientos = "Vacunación antirrábica",
        recomendaciones = "Próxima dosis en 1 año",
        horaInicio = "10:30",
        horaFin = "10:45"
    )
)
```

---

## 🔄 Flujo Completo de Testing

```
1. Compilar proyecto
   ↓
2. Instalar en emulador/dispositivo
   ↓
3. Testear UI (sin backend)
   - Verificar que pantalla se renderiza
   - Verificar botones/tabs
   ↓
4. Configurar backend
   - Implementar endpoints
   - Crear datos de prueba
   ↓
5. Testear integración
   - Dueño: Ver historial
   - Veterinario: Actualizar cita
   - Dueño: Ver cambios reflejados
   ↓
6. Testing de edge cases
   - Citas sin datos médicos
   - Campos vacíos
   - Horas mal formateadas
   ↓
7. Deployment
```

---

## 📊 Checklist Final

- [ ] Proyecto compila sin errores
- [ ] Imports están correctos
- [ ] HistorialCitasScreen se muestra
- [ ] Tabs funcionan (Pendientes/Completadas)
- [ ] Veterinario puede expandir ficha médica
- [ ] Backend endpoints implementados
- [ ] CircularProgressIndicator muestra al cargar
- [ ] Datos se muestran correctamente en tarjetas
- [ ] Actualizar cita guarda en backend
- [ ] Dueño ve cambios actualizados

---

## 💡 Tips para Debugging

### Ver Logs
```
Android Studio → View → Tool Windows → Logcat
Filtrar por: "myvet", "retrofit", "http"
```

### Inspeccionar Network
```
Android Studio → Profiler → Network
Revisar requests/responses HTTP
```

### Revisar Base de Datos
```
Si usas Room:
Android Studio → Device File Explorer → data/data/com.proyect.myvet/databases
```

---

## 📞 Soporte

Si encuentras problemas:

1. **Revisa los logs** en Logcat
2. **Verifica BACKEND_SPECIFICATIONS.md** para endpoints correctos
3. **Consulta CAMBIOS_CITAS.md** para lista de archivos modificados
4. **Asegúrate de que el backend está corriendo**
5. **Verifica token de autenticación sea válido**

---

## 🎉 ¡Listo!

Una vez que todo funcione, tendrás:

✅ Historial de citas con tabs (Pendientes/Completadas)
✅ Información del tutor en cada cita
✅ Ficha médica completa en citas completadas
✅ Veterinario puede registrar diagnóstico y procedimientos
✅ Horas de atención registradas
✅ Interfaz cohesiva y bonita


