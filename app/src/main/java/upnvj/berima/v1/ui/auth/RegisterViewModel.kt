package upnvj.berima.v1.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.repository.AuthRepository
import javax.inject.Inject

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToHome: Boolean = false,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, error = null)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, error = null)
    }

    fun signUp() {
        val state = _uiState.value
        when {
            state.name.isBlank() -> {
                _uiState.value = state.copy(error = "Nama tidak boleh kosong")
                return
            }
            state.email.isBlank() -> {
                _uiState.value = state.copy(error = "Email tidak boleh kosong")
                return
            }
            state.password.length < 8 -> {
                _uiState.value = state.copy(error = "Password minimal 8 karakter")
                return
            }
            state.password != state.confirmPassword -> {
                _uiState.value = state.copy(error = "Password tidak cocok")
                return
            }
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signUp(state.name.trim(), state.email.trim(), state.password)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, navigateToHome = true)
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("email-already-in-use", ignoreCase = true) == true ->
                            "Email sudah terdaftar"
                        e.message?.contains("domain", ignoreCase = true) == true ->
                            "Format email tidak valid"
                        else -> e.message ?: "Terjadi kesalahan"
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, error = message)
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun onNavigatedToHome() {
        _uiState.value = _uiState.value.copy(navigateToHome = false)
    }
}
