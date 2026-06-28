package upnvj.berima.v1.ui.verification

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.data.model.VerificationStatus
import upnvj.berima.v1.data.model.VerificationSubmission
import upnvj.berima.v1.data.model.VerificationType
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.StorageRepository
import upnvj.berima.v1.data.repository.VerificationRepository
import upnvj.berima.v1.ui.common.AppStrings
import javax.inject.Inject

@HiltViewModel
class IdentityVerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val verificationRepository: VerificationRepository
) : ViewModel() {

    private val _submission = MutableStateFlow<VerificationSubmission?>(null)
    val submission: StateFlow<VerificationSubmission?> = _submission.asStateFlow()

    private val _selectedUri = MutableStateFlow<Uri?>(null)
    val selectedUri: StateFlow<Uri?> = _selectedUri.asStateFlow()

    private val _selectedFileName = MutableStateFlow<String?>(null)
    val selectedFileName: StateFlow<String?> = _selectedFileName.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    init {
        val uid = authRepository.currentUserId
        if (uid != null) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    verificationRepository
                        .observeSubmissions(uid, VerificationType.IDENTITY)
                        .collect { submissions ->
                            _submission.value = submissions.firstOrNull()
                            _isLoading.value = false
                        }
                } catch (e: Exception) {
                    _error.value = e.message
                    _isLoading.value = false
                }
            }
        }
    }

    fun onFileSelected(uri: Uri?) {
        _selectedUri.value = uri
        _selectedFileName.value = uri?.lastPathSegment?.substringAfterLast('/')
    }

    fun onNoteChange(value: String) {
        _note.value = value.take(Validation.MAX_VERIFICATION_NOTE_LENGTH)
    }

    fun submit() {
        val uid = authRepository.currentUserId
        val uri = _selectedUri.value
        val existing = _submission.value

        when {
            uid == null -> {
                _error.value = AppStrings.VERIFICATION_ERROR_LOGIN_REQUIRED
                return
            }
            existing?.status == VerificationStatus.PENDING -> {
                _error.value = AppStrings.VERIFICATION_ERROR_PENDING_DUPLICATE
                return
            }
            existing?.status == VerificationStatus.APPROVED -> {
                _error.value = AppStrings.VERIFICATION_ERROR_ALREADY_APPROVED
                return
            }
            uri == null -> {
                _error.value = AppStrings.IDENTITY_ERROR_FILE_REQUIRED
                return
            }
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            var uploadedPath: String? = null
            try {
                val submissionId = verificationRepository.newSubmissionId()
                val metadata = storageRepository
                    .uploadIdentityEvidence(uid, submissionId, uri)
                    .getOrElse { throw it }
                uploadedPath = metadata.storagePath
                verificationRepository.createIdentitySubmission(
                    submissionId = submissionId,
                    userId = uid,
                    storagePath = metadata.storagePath,
                    fileName = metadata.fileName,
                    contentType = metadata.contentType,
                    note = note.value
                ).getOrElse { throw it }

                _selectedUri.value = null
                _selectedFileName.value = null
                _note.value = ""
                _success.value = AppStrings.IDENTITY_SUBMIT_SUCCESS
            } catch (e: Exception) {
                uploadedPath?.let { storageRepository.deleteFile(it) }
                _error.value = e.message
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }
}
