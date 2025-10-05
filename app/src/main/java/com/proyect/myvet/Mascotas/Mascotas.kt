package com.proyect.myvet.mascotas

import java.io.Serializable

data class Mascota(
    val id: Long = System.currentTimeMillis(),
    val nombre: String,
    val especie: String, // Ej: "Perro", "Gato"
    val raza: String,
    val fechaNacimiento: String,
    val sexo: String // Ej: "Macho", "Hembra"
) : Serializable