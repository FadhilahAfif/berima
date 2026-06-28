package upnvj.berima.v1.ui.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.User
import upnvj.berima.v1.data.model.VerificationSubmission
import upnvj.berima.v1.data.model.VerificationType
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.VerificationRepository
import javax.inject.Inject

@HiltViewModel
class VerificationCenterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val verificationRepository: VerificationRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _identitySubmission = MutableStateFlow<VerificationSubmission?>(null)
    val identitySubmission: StateFlow<VerificationSubmission?> = _identitySubmission.asStateFlow()

    private val _skillSubmissions = MutableStateFlow<List<VerificationSubmission>>(emptyList())
    val skillSubmissions: StateFlow<List<VerificationSubmission>> = _skillSubmissions.asStateFlow()

    init {
        val uid = authRepository.currentUserId
        if (uid != null) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    authRepository.observeUser(uid).collect { _user.value = it }
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _isLoading.value = false
                }
            }
            viewModelScope.launch {
                try {
                    verificationRepository
                        .observeSubmissions(uid, VerificationType.IDENTITY)
                        .collect { submissions ->
                            _identitySubmission.value = submissions.firstOrNull()
                        }
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
            viewModelScope.launch {
                try {
                    verificationRepository
                        .observeSubmissions(uid, VerificationType.SKILL)
                        .collect { submissions ->
                            _skillSubmissions.value = submissions
                        }
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
