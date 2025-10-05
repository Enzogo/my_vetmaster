package com.proyect.myvet.historial

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HistorialManager {
    private const val PREFS_NAME = "historial_prefs"
    private const val KEY_HISTORIAL = "lista_citas"
    private val gson = Gson()

    fun guardarCita(context: Context, cita: HistorialCita) {
        val citas = obtenerCitas(context).toMutableList()
        citas.add(0, cita)
        val jsonString = gson.toJson(citas)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_HISTORIAL, jsonString).apply()
    }

    fun obtenerCitas(context: Context): List<HistorialCita> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_HISTORIAL, null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<HistorialCita>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    // --- ¡NUEVA FUNCIÓN! ---
    // Esta función elimina una cita por su ID.
    fun eliminarCita(context: Context, citaId: Long) {
        val citas = obtenerCitas(context).toMutableList()
        val citaAEliminar = citas.find { it.id == citaId }
        if (citaAEliminar != null) {
            citas.remove(citaAEliminar)
            val jsonString = gson.toJson(citas)
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_HISTORIAL, jsonString).apply()
        }
    }
}