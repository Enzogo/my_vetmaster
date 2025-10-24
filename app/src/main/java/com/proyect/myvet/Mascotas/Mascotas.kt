package com.proyect.myvet.mascotas

data class Mascota(
    val id: Long = System.currentTimeMillis(),
    val nombre: String,
    val especie: String,
    val raza: String? = null,
    val fechaNacimiento: String? = null,
    val sexo: String? = null
)