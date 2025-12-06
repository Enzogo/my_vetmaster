                onValueChange = {},
                label = { Text("Veterinario") },
                leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVet) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = textFieldColors
            )
            ExposedDropdownMenu(expanded = expandedVet, onDismissRequest = { expandedVet = false }) {
                // Opción para deseleccionar
                DropdownMenuItem(
                    text = { Text("Sin seleccionar", color = Color.Gray) },
                    onClick = {
                        veterinarioSeleccionadoId = null
                        expandedVet = false
                    }
                )
                // Listar veterinarios disponibles
                if (veterinarios.isEmpty()) {
                    DropdownMenuItem(text = { Text("No hay veterinarios disponibles") }, onClick = { expandedVet = false })
                } else {
                    veterinarios.forEach { v ->
                        DropdownMenuItem(
                            text = { Text(v.nombre ?: v.email ?: "(sin nombre)", color = Color(0xFF7DA581), fontWeight = FontWeight.Medium) },
                            onClick = {
                                veterinarioSeleccionadoId = v.id
                                expandedVet = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// ✅ MODIFICADO: Botón agendar pasa veterinarioId
val response = api.createCita(CitaCreateRequest(fechaIso, motivoCita, mascotaId, veterinarioSeleccionadoId))
```

---

## 🔄 Flujo de Funcionamiento

1. **Usuario abre "Agendar Cita"**
   - Se cargan automáticamente: mascotas y veterinarios disponibles

2. **Selector muestra 4 campos:**
   - ✅ Mascota (obligatorio)
   - ✅ Veterinario (opcional) ← NUEVO
   - ✅ Motivo (obligatorio)
   - ✅ Fecha y Hora (obligatorios)

3. **Usuario elige veterinario**
   - Puede seleccionar uno de la lista
   - O dejar "Sin seleccionar"

4. **Al hacer clic en "Agendar"**
   - Se envía al backend:
   ```json
   {
     "fechaIso": "2025-01-15 14:30",
     "motivo": "Vacunación",
     "mascotaId": "64a1b2c3d4e5f6g7h8i9j0k1",
     "veterinarioId": "50a1b2c3d4e5f6g7h8i9j0k1"  // ← Incluido si está seleccionado
   }
   ```

5. **Backend persiste en MongoDB**
   ```javascript
   {
     ownerId: ObjectId(...),
     mascotaId: ObjectId(...),
     veterinarioId: ObjectId(...),  // ← Almacenado
     fechaIso: "2025-01-15 14:30",
     motivo: "Vacunación",
     estado: "pendiente"
   }
   ```

---

## ✨ Características

✅ **Veterinario opcional** - Si no selecciona uno, se crea la cita sin asignar  
✅ **Carga automática** - Se cargan veterinarios al abrir la pantalla  
✅ **UI consistente** - Mismo estilo que selector de mascota  
✅ **Compatible con backend** - No requiere cambios en backend_myvet  
✅ **Manejo de errores** - Toast si falla la carga  

---

## 🚀 Próximas Mejoras (Opcional)

- [ ] Mostrar disponibilidad del veterinario por hora
- [ ] Filtrar citas por veterinario en historial
- [ ] Enviar notificación al veterinario cuando se asigna cita
- [ ] Opción para que dueño cambie veterinario después de agendar

---

## ⚠️ Nota sobre el Error del Terminal

El error que ves es de **Android Studio (IDE), NO de tu app**:
```
java.lang.UnsatisfiedLinkError: Native library (jnidispatch.dll) not found
```

Esto es un bug conocido de Android Studio en Windows. **NO afecta la compilación ni la app**.

**Solución:** Simplemente ignora el error. Tu app compilará normalmente.

---

## ✅ Verificación Final

Para probar que todo funciona:

1. Abre la app y ve a **"Agendar Cita"**
2. Deberías ver 4 opciones (mascota, **veterinario**, motivo, fecha/hora)
3. Intenta agendar una cita:
   - **Con veterinario seleccionado** ✓
   - **Sin veterinario** ✓
4. Ve al historial y verifica que aparezca el veterinario (si fue asignado)

---

## 📦 Archivos Modificados

- ✏️ `app/src/main/java/com/proyect/myvet/network/OwnerApi.kt`
- ✏️ `app/src/main/java/com/proyect/myvet/citas/CitasScreen.kt`

**Ningún cambio en backend_myvet (ya soporta la funcionalidad)**


