package com.proyect.myvet.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.proyect.myvet.network.CitaDto
import com.proyect.myvet.network.MascotaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OwnerUiState(
    val loading: Boolean = false,
    val mascotas: List<MascotaDto> = emptyList(),
    val citas: List<CitaDto> = emptyList(),
    val error: String? = null
)

class OwnerViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = OwnerRepository(app.applicationContext)

    private val _state = MutableStateFlow(OwnerUiState())
    val state: StateFlow<OwnerUiState> = _state

    fun refreshLists() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val mascotasR = repo.loadMascotas()
            val citasR = repo.loadCitas()
            _state.value = _state.value.copy(
                loading = false,
                mascotas = mascotasR.getOrDefault(emptyList()),
                citas = citasR.getOrDefault(emptyList()),
                error = mascotasR.exceptionOrNull()?.message
                    ?: citasR.exceptionOrNull()?.message
            )
        }
    }

    fun addMascota(
        nombre: String,
        especie: String,
        raza: String?,
        onDone: () -> Unit,
        onError: (String) -> Unit
    ) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val res = repo.addMascota(nombre.trim(), especie.trim(), raza?.trim(), null, null)
            _state.value = _state.value.copy(loading = false)
            if (res.isSuccess) onDone() else onError(res.exceptionOrNull()?.message ?: "Error al crear mascota")
        }
    }

    fun addCita(
        fechaIso: String,
        motivo: String,
        mascotaId: String,
        onDone: () -> Unit,
        onError: (String) -> Unit
    ) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val res = repo.addCita(fechaIso.trim(), motivo.trim(), mascotaId)
            _state.value = _state.value.copy(loading = false)
            if (res.isSuccess) onDone() else onError(res.exceptionOrNull()?.message ?: "Error al crear cita")
        }
    }

    fun updateMascota(
        id: String,
        nombre: String,
        especie: String,
        raza: String?,
        fechaNacimiento: String?,
        sexo: String?,
        onDone: () -> Unit,
        onError: (String) -> Unit
    ) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val res = repo.updateMascota(id, nombre.trim(), especie.trim(), raza?.trim(), fechaNacimiento?.trim(), sexo?.trim())
            _state.value = _state.value.copy(loading = false)
            if (res.isSuccess) {
                refreshLists()
                onDone()
            } else {
                onError(res.exceptionOrNull()?.message ?: "Error al actualizar mascota")
            }
        }
    }

    fun deleteMascota(
        id: String,
        onDone: () -> Unit,
        onError: (String) -> Unit
    ) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val res = repo.deleteMascota(id)
            _state.value = _state.value.copy(loading = false)
            if (res.isSuccess) {
                refreshLists()
                onDone()
            } else {
                onError(res.exceptionOrNull()?.message ?: "Error al eliminar mascota")
            }
        }
    }
}