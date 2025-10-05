package com.proyect.myvet.mascotas

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MascotaManager {
    private const val PREFS_NAME = "mascotas_prefs"
    private const val KEY_MASCOTAS = "lista_mascotas"
    private val gson = Gson()

    private fun guardarLista(context: Context, mascotas: List<Mascota>) {
        val jsonString = gson.toJson(mascotas)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_MASCOTAS, jsonString).apply()
    }

    fun obtenerMascotas(context: Context): List<Mascota> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_MASCOTAS, null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Mascota>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    fun obtenerMascotaPorId(context: Context, id: Long): Mascota? {
        return obtenerMascotas(context).find { it.id == id }
    }

    fun guardarMascota(context: Context, mascota: Mascota) {
        val mascotas = obtenerMascotas(context).toMutableList()
        mascotas.add(mascota)
        guardarLista(context, mascotas)
    }

    // --- ¡NUEVA FUNCIÓN! ---
    fun actualizarMascota(context: Context, mascotaActualizada: Mascota) {
        val mascotas = obtenerMascotas(context).toMutableList()
        val index = mascotas.indexOfFirst { it.id == mascotaActualizada.id }
        if (index != -1) {
            mascotas[index] = mascotaActualizada
            guardarLista(context, mascotas)
        }
    }

    // --- ¡NUEVA FUNCIÓN! ---
    fun eliminarMascota(context: Context, mascotaId: Long) {
        val mascotas = obtenerMascotas(context).toMutableList()
        mascotas.removeAll { it.id == mascotaId }
        guardarLista(context, mascotas)
    }
}