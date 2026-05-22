package upnvj.berima.v1.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.UserRole
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.StorageRepository
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = _bio.asStateFlow()

    private val _faculty = MutableStateFlow("")
    val faculty: StateFlow<String> = _faculty.asStateFlow()

    private val _role = MutableStateFlow(UserRole.BOTH)
    val role: StateFlow<String> = _role.asStateFlow()

    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> = _photoUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = authRepository.currentUserId
            if (uid == null) {
                _error.value = "Sesi tidak ditemukan"
                _isLoading.value = false
                return@launch
            }

            authRepository.getUser(uid)
                .onSuccess { user ->
                    user?.let {
                        _name.value = it.name
                        _bio.value = it.bio.orEmpty()
                        _faculty.value = it.faculty.orEmpty()
                        _role.value = it.role
                        _photoUrl.value = it.photoUrl
                    }
                }
                .onFailure {
                    _error.value = it.message ?: "Gagal memuat profil"
                }

            _isLoading.value = false
        }
    }

    fun onNameChange(v: String) {
        _name.value = v.take(50)
    }

    fun onBioChange(v: String) {
        _bio.value = v.take(150)
    }

    fun onFacultyChange(v: String) {
        _faculty.value = v
    }

    fun onRoleChange(v: String) {
        _role.value = v
    }

    fun onPhotoPicked(uri: Uri, context: Context) {
        val uid = authRepository.currentUserId
        if (uid == null) {
            _error.value = "Sesi tidak ditemukan"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            storageRepository.uploadProfilePhoto(uid, uri)
                .onSuccess { url ->
                    _photoUrl.value = url
                }
                .onFailure {
                    _error.value = it.message ?: "Gagal mengunggah foto"
                }
            _isLoading.value = false
        }
    }

    fun save() {
        val uid = authRepository.currentUserId
        if (uid == null) {
            _error.value = "Sesi tidak ditemukan"
            return
        }

        val normalizedName = _name.value.trim()
        if (normalizedName.isBlank()) {
            _error.value = "Nama tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            authRepository.updateProfile(
                uid = uid,
                name = normalizedName,
                bio = _bio.value.trim().takeIf { it.isNotBlank() },
                faculty = _faculty.value.trim().takeIf { it.isNotBlank() },
                role = _role.value,
                photoUrl = _photoUrl.value
            ).onSuccess {
                _isSaved.value = true
            }.onFailure {
                _error.value = it.message ?: "Gagal menyimpan profil"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
