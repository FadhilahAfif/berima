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

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val navigateToHome: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null, message = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null, message = null)
    }

    fun signIn() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Email dan password tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signIn(state.email.trim(), state.password)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, navigateToHome = true)
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("no user record", ignoreCase = true) == true ||
                        e.message?.contains("user-not-found", ignoreCase = true) == true ->
                            "Email tidak terdaftar"
                        e.message?.contains("password", ignoreCase = true) == true ||
                        e.message?.contains("wrong-password", ignoreCase = true) == true ->
                            "Password salah"
                        else -> "Terjadi kesalahan saat masuk. Silakan coba lagi."
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, error = message)
                }
            )
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, message = null)
            val result = authRepository.signInWithGoogle(idToken)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, navigateToHome = true)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Gagal masuk dengan Google. Silakan coba lagi."
                    )
                }
            )
        }
    }

    fun sendPasswordResetEmail() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null, message = null)
            val result = authRepository.sendPasswordResetEmail(state.email)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Instruksi reset password sudah dikirim ke email kamu."
                    )
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("valid", ignoreCase = true) == true ->
                            "Masukkan email yang valid terlebih dahulu"
                        e.message?.contains("user", ignoreCase = true) == true ->
                            "Email tidak terdaftar"
                        else -> "Gagal mengirim email reset password. Silakan coba lagi."
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, error = message)
                }
            )
        }
    }

    fun onGoogleSignInCancelled() {
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun onNavigatedToHome() {
        _uiState.value = _uiState.value.copy(navigateToHome = false)
    }
}
