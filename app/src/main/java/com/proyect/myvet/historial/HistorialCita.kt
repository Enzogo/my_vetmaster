package com.proyect.myvet.historial

import java.io.Serializable

data class HistorialCita(
    val id: Long,
    val mascota: String,
    val dueno: String,
    val motivo: String,
    val fecha: String,
    val hora: String
) : Serializable