   }
   ```

5. **Backend persiste en MongoDB:**
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

## ✨ Características

✅ **Veterinario opcional** - Si no selecciona, la cita se crea sin asignar  
✅ **Carga automática** - Se cargan veterinarios registrados al abrir pantalla  
✅ **UI consistente** - Mismo estilo que selector de mascota  
✅ **Compatible con backend** - Backend ya soporta esta funcionalidad  
✅ **Manejo de errores** - Toast si falla la carga  

## 📝 Archivos Modificados

- ✏️ `app/src/main/java/com/proyect/myvet/network/OwnerApi.kt`
- ✏️ `app/src/main/java/com/proyect/myvet/citas/CitasScreen.kt`

## ✅ Verificación

Para probar que todo funciona:

1. Abre la app → "Agendar Cita"
2. Verifica que aparezca el selector de veterinario
3. Intenta agendar una cita con y sin veterinario
4. Verifica en el historial que se haya guardado correctamente

