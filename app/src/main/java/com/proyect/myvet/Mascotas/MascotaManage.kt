package com.proyect.myvet.mascotas

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MascotaManager {
    private const val PREFS = "mascotas_prefs"
    private const val KEY = "mascotas_json"
    private val gson = Gson()
    private val listType = object : TypeToken<MutableList<Mascota>>() {}.type

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun loadAll(context: Context): MutableList<Mascota> {
        val json = getPrefs(context).getString(KEY, null) ?: return mutableListOf()
        return try {
            gson.fromJson(json, listType) ?: mutableListOf()
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    private fun saveAll(context: Context, list: List<Mascota>) {
        getPrefs(context).edit().putString(KEY, gson.toJson(list)).apply()
    }

    fun obtenerMascotas(context: Context): List<Mascota> =
        loadAll(context)

    fun obtenerMascotaPorId(context: Context, id: Long): Mascota? =
        loadAll(context).firstOrNull { it.id == id }

    fun guardarMascota(context: Context, mascota: Mascota) {
        val list = loadAll(context)
        list.add(mascota)
        saveAll(context, list)
    }

    fun actualizarMascota(context: Context, mascota: Mascota) {
        val list = loadAll(context)
        val idx = list.indexOfFirst { it.id == mascota.id }
        if (idx >= 0) {
            list[idx] = mascota
            saveAll(context, list)
        }
    }

    fun eliminarMascota(context: Context, id: Long) {
        val list = loadAll(context)
        val newList = list.filterNot { it.id == id }
        saveAll(context, newList)
    }
}