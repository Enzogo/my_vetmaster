    const duenio = await Usuario.findById(cita.duenioId);
    
    res.json({
      id: cita._id,
      fechaIso: cita.fechaIso,
      motivo: cita.motivo,
      mascotaId: cita.mascotaId._id,
      estado: cita.estado,
      horaInicio: cita.horaInicio,
      horaFin: cita.horaFin,
      diagnostico: cita.diagnostico,
      procedimientos: cita.procedimientos,
      recomendaciones: cita.recomendaciones,
      veterinarioNombre: cita.veterinarioId.nombre,
      duenioNombre: duenio.nombre,
      duenioTelefono: duenio.telefono,
      duenioCorreo: duenio.email,
      nombreMascota: cita.mascotaId.nombre
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});
```

## 3. Migraciones de Base de Datos

Si usas Mongoose, asegúrate de que el schema tenga estos campos:

```javascript
const citaSchema = new Schema({
  // ... campos existentes ...
  
  // Nuevos campos
  horaInicio: {
    type: String,
    match: /^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/,
    required: false
  },
  horaFin: {
    type: String,
    match: /^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/,
    required: false
  },
  diagnostico: {
    type: String,
    required: false
  },
  procedimientos: {
    type: String,
    required: false
  },
  recomendaciones: {
    type: String,
    required: false
  },
  
  updatedAt: {
    type: Date,
    default: Date.now
  }
});
```

## 4. Testing

### Test 1: Obtener citas pendientes
```bash
curl -H "Authorization: Bearer TOKEN" \
  http://localhost:3000/api/owners/me/citas/pendientes
```

### Test 2: Obtener citas completadas
```bash
curl -H "Authorization: Bearer TOKEN" \
  http://localhost:3000/api/owners/me/citas/completadas
```

### Test 3: Actualizar cita con horas
```bash
curl -X PUT \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "horaInicio": "14:30",
    "horaFin": "15:00"
  }' \
  http://localhost:3000/api/owners/me/citas/cita-uuid-1
```

## 5. Consideraciones de Seguridad

1. **Autenticación:** Verificar token JWT en todos los endpoints
2. **Autorización:** 
   - Solo el dueño puede ver sus citas
   - Solo el veterinario que atendió puede actualizar
3. **Validaciones:**
   - Formato de horas HH:mm
   - horaFin >= horaInicio
   - Cita debe estar en estado "completada"
4. **Rate Limiting:** Considerar implementar para el endpoint de refresh

## 6. Posibles Mejoras Futuras

1. Agregar notificaciones cuando se actualiza una cita
2. Historial de cambios en citas
3. Exportar citas como PDF
4. Integración con calendarios
5. Recordatorios automáticos

