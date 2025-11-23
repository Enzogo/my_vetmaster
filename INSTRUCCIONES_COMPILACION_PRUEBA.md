  ☐ Acepta horaInicio en formato HH:mm
  ☐ Acepta horaFin en formato HH:mm
  ☐ Valida que horaFin >= horaInicio
  ☐ Solo permite actualizar si cita está completada
  ☐ Retorna cita actualizada con todos los campos
```

## 6. Pruebas de API (con Postman/cURL)

### Obtener citas pendientes:
```bash
curl -X GET \
  -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:3000/api/owners/me/citas/pendientes
```

### Obtener citas completadas:
```bash
curl -X GET \
  -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:3000/api/owners/me/citas/completadas
```

### Actualizar cita con horas:
```bash
curl -X PUT \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "horaInicio": "14:30",
    "horaFin": "15:00"
  }' \
  http://localhost:3000/api/owners/me/citas/CITA_ID
```

## 7. Troubleshooting

### La aplicación no muestra citas
```
1. Verifica que haya citas en la base de datos
2. Verifica que el usuario esté autenticado correctamente
3. Revisa los logs de error en Logcat
4. Verifica que los endpoints retornen datos correctamente (con Postman)
```

### Las horas no se guardan
```
1. Verifica que el botón "Guardar" esté visible
2. Verifica que no haya error en el Toast (revisa Logcat)
3. Verifica en el backend que la cita se actualizó (check base de datos)
4. Verifica formato: debe ser HH:mm (ej: 14:30, no 2:30 PM)
```

### Auto-refresh no funciona
```
1. Verifica que el dispositivo tenga conexión a internet
2. Espera 30 segundos (el ciclo de refresh)
3. Presiona el botón de refresh manual para verificar que funciona
4. Si el manual funciona pero el auto no, reinicia la app
```

## 8. Performance

### Consideraciones:
- Auto-refresh cada 30 segundos es un intervalo razonable
- Si necesitas más frecuencia, considera usar WebSockets
- Para menos frecuencia, cambia `delay(30000)` a otro valor
- Optimiza las consultas en el backend si hay muchas citas

## 9. Próximos Pasos

Después de validar que todo funciona:

1. ✅ Implementar notificaciones push cuando cambia estado de cita
2. ✅ Agregar edición de diagnóstico/procedimientos desde el cliente vet
3. ✅ Agregar historial de cambios
4. ✅ Exportar citas como PDF
5. ✅ Agregar filtros avanzados (por veterinario, por mascota, etc)

## 10. Contacto y Soporte

Si encuentras problemas:
1. Revisa este documento
2. Revisa los logs en Logcat
3. Verifica que el backend esté implementado correctamente
4. Contacta al equipo de desarrollo

---

**Última actualización:** 2025-01-23
**Versión:** 1.0
**Estado:** Listo para producción (con backend implementado)

