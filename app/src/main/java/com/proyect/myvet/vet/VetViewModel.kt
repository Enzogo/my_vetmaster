package com.proyect.myvet.vet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.proyect.myvet.network.CitaSummary
import com.proyect.myvet.network.MascotaSummary
import com.proyect.myvet.network.OwnerSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class VetUiState(
    val loading: Boolean = false,
    val owners: List<OwnerSummary> = emptyList(),
    val citas: List<CitaSummary> = emptyList(),
    val mascotas: List<MascotaSummary> = emptyList(),
    val error: String? = null
)

class VetViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = VetRepository(app.applicationContext)

    private val _state = MutableStateFlow(VetUiState())
    val state: StateFlow<VetUiState> = _state

    fun loadAll() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val ownersR = repo.owners()
            val citasR = repo.citas()
            val mascotasR = repo.mascotas()

            val error = ownersR.exceptionOrNull()?.message
                ?: citasR.exceptionOrNull()?.message
                ?: mascotasR.exceptionOrNull()?.message

            _state.value = _state.value.copy(
                loading = false,
                owners = ownersR.getOrDefault(emptyList()),
                citas = citasR.getOrDefault(emptyList()),
                mascotas = mascotasR.getOrDefault(emptyList()),
                error = error
            )
        }
    }
}