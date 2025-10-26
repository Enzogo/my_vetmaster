package com.proyect.myvet.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val role: String? = null,
    val email: String? = null,
    val nombre: String? = null
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AuthRepository(app.applicationContext)

    private val _state = MutableStateFlow(
        AuthUiState(
            isLoggedIn = repo.isLoggedIn(),
            role = repo.getRole(),
            email = repo.getEmail(),
            nombre = repo.getNombre()
        )
    )
    val state: StateFlow<AuthUiState> = _state

    fun validateSession() {
        _state.value = _state.value.copy(
            isLoggedIn = repo.isLoggedIn(),
            role = repo.getRole(),
            email = repo.getEmail(),
            nombre = repo.getNombre()
        )
    }

    fun login(email: String, password: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val res = repo.login(email, password)
            _state.value = _state.value.copy(
                loading = false,
                isLoggedIn = res.isSuccess && repo.isLoggedIn(),
                role = repo.getRole(),
                email = repo.getEmail(),
                nombre = repo.getNombre(),
                error = res.exceptionOrNull()?.message
            )
            if (res.isSuccess) onSuccess() else onError(res.exceptionOrNull()?.message ?: "Error de login")
        }
    }

    fun register(email: String, password: String, role: String, nombre: String? = null, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val res = repo.register(email, password, role, nombre)
            _state.value = _state.value.copy(
                loading = false,
                isLoggedIn = res.isSuccess && repo.isLoggedIn(),
                role = repo.getRole(),
                email = repo.getEmail(),
                nombre = repo.getNombre(),
                error = res.exceptionOrNull()?.message
            )
            if (res.isSuccess) onSuccess() else onError(res.exceptionOrNull()?.message ?: "Error de registro")
        }
    }

    fun logout() {
        repo.logout()
        _state.value = _state.value.copy(isLoggedIn = false, role = null, email = null, nombre = null)
    }
}