package upnvj.berima.v1.ui.listing

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.ListingRepository
import upnvj.berima.v1.data.repository.StorageRepository
import upnvj.berima.v1.navigation.Screen
import upnvj.berima.v1.ui.common.AppStrings
import javax.inject.Inject

@HiltViewModel
class EditListingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listingRepository: ListingRepository,
    private val storageRepository: StorageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    companion object {
        private const val TAG = "EditListingViewModel"
    }

    private val listingId: String =
        checkNotNull(savedStateHandle[Screen.EditListing.ARG_LISTING_ID])

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price.asStateFlow()

    private val _deliveryTimeHours = MutableStateFlow("")
    val deliveryTimeHours: StateFlow<String> = _deliveryTimeHours.asStateFlow()

    private val _tags = MutableStateFlow("")
    val tags: StateFlow<String> = _tags.asStateFlow()

    private val _existingThumbnailUrl = MutableStateFlow<String?>(null)
    val existingThumbnailUrl: StateFlow<String?> = _existingThumbnailUrl.asStateFlow()

    private val _existingThumbnailStoragePath = MutableStateFlow<String?>(null)
    val existingThumbnailStoragePath: StateFlow<String?> = _existingThumbnailStoragePath.asStateFlow()

    private val _selectedThumbnailUri = MutableStateFlow<Uri?>(null)
    val selectedThumbnailUri: StateFlow<Uri?> = _selectedThumbnailUri.asStateFlow()

    private val _removeExistingThumbnail = MutableStateFlow(false)
    val removeExistingThumbnail: StateFlow<Boolean> = _removeExistingThumbnail.asStateFlow()

    private val _isPolicyAccepted = MutableStateFlow(false)
    val isPolicyAccepted: StateFlow<Boolean> = _isPolicyAccepted.asStateFlow()

    private val _isActive = MutableStateFlow(true)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    init {
        loadListing()
    }

    private fun loadListing() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = listingRepository.getListing(listingId)
            result.fold(
                onSuccess = { listing ->
                    listing?.let {
                        _title.value = it.title
                        _category.value = it.category
                        _description.value = it.description
                        _price.value = it.price.toString()
                        _deliveryTimeHours.value = it.deliveryTimeHours.toString()
                        _tags.value = it.tags.joinToString(", ")
                        _existingThumbnailUrl.value = it.thumbnailUrl
                        _existingThumbnailStoragePath.value = it.thumbnailStoragePath
                        _isActive.value = it.isActive
                    }
                },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun onTitleChange(value: String) {
        if (value.length <= Validation.MAX_LISTING_TITLE_LENGTH) _title.value = value
    }

    fun onCategoryChange(value: String) { _category.value = value }

    fun onDescriptionChange(value: String) {
        if (value.length <= Validation.MAX_LISTING_DESCRIPTION_LENGTH) _description.value = value
    }

    fun onPriceChange(value: String) {
        if (value.all { it.isDigit() }) _price.value = value
    }

    fun onDeliveryTimeChange(value: String) {
        if (value.all { it.isDigit() }) _deliveryTimeHours.value = value
    }

    fun onTagsChange(value: String) { _tags.value = value }

    fun onThumbnailSelected(uri: Uri?) {
        _selectedThumbnailUri.value = uri
        if (uri != null) _removeExistingThumbnail.value = false
    }

    fun removeThumbnail() {
        _selectedThumbnailUri.value = null
        _removeExistingThumbnail.value = _existingThumbnailUrl.value != null
    }

    fun onPolicyAcceptedChange(value: Boolean) {
        _isPolicyAccepted.value = value
    }

    fun submit() {
        val titleVal = _title.value.trim()
        val descVal = _description.value.trim()
        val priceVal = _price.value.toLongOrNull()
        val deliveryVal = _deliveryTimeHours.value.toIntOrNull()

        if (titleVal.isBlank()) { _error.value = "Judul tidak boleh kosong"; return }
        if (descVal.isBlank()) { _error.value = "Deskripsi tidak boleh kosong"; return }
        if (priceVal == null || priceVal <= 0) { _error.value = "Harga tidak valid"; return }
        if (deliveryVal == null || deliveryVal <= 0 || deliveryVal > Validation.MAX_DELIVERY_TIME_HOURS) {
            _error.value = "Waktu pengerjaan harus antara 1–${Validation.MAX_DELIVERY_TIME_HOURS} jam"
            return
        }

        if (!_isPolicyAccepted.value) {
            _error.value = AppStrings.LISTING_POLICY_ERROR
            return
        }

        val tagList = _tags.value
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        viewModelScope.launch {
            _isLoading.value = true
            val uid = authRepository.currentUserId
            if (uid == null) {
                _isLoading.value = false
                _error.value = "Sesi berakhir, silakan login ulang"
                return@launch
            }
            val user = authRepository.getUser(uid).getOrNull()
            val previousThumbnailStoragePath = _existingThumbnailStoragePath.value
            var uploadedThumbnailPath: String? = null
            val thumbnailUpload = _selectedThumbnailUri.value?.let { uri ->
                val uploadResult = storageRepository.uploadListingThumbnail(uid, listingId, uri)
                uploadResult.getOrElse {
                    _isLoading.value = false
                    Log.e(TAG, "Gagal mengunggah thumbnail listing", it)
                    _error.value = "Gagal mengunggah gambar listing"
                    return@launch
                }
            }
            uploadedThumbnailPath = thumbnailUpload?.storagePath
            val thumbnailUrl = thumbnailUpload?.downloadUrl
            val thumbnailStoragePath = thumbnailUpload?.storagePath
            val clearThumbnail = _removeExistingThumbnail.value && thumbnailUpload == null
            val result = listingRepository.updateListing(
                listingId = listingId,
                title = titleVal,
                description = descVal,
                category = _category.value,
                price = priceVal,
                deliveryTimeHours = deliveryVal,
                tags = tagList,
                thumbnailUrl = thumbnailUrl,
                thumbnailStoragePath = thumbnailStoragePath,
                clearThumbnail = clearThumbnail,
                sellerIdentityVerified = user?.isIdentityVerified,
                sellerVerifiedSkillBadges = user?.verifiedSkillBadges,
                policyAcceptedAt = Timestamp.now()
            )
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    if (!previousThumbnailStoragePath.isNullOrBlank() &&
                        previousThumbnailStoragePath != thumbnailStoragePath
                    ) {
                        storageRepository.deleteFile(previousThumbnailStoragePath)
                    }
                    _existingThumbnailUrl.value = thumbnailUrl ?: if (clearThumbnail) null else _existingThumbnailUrl.value
                    _existingThumbnailStoragePath.value = thumbnailStoragePath ?: if (clearThumbnail) null else _existingThumbnailStoragePath.value
                    _success.value = true
                },
                onFailure = {
                    uploadedThumbnailPath?.let { path ->
                        storageRepository.deleteFile(path)
                    }
                    _error.value = it.message
                }
            )
        }
    }

    fun deactivateListing() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = listingRepository.setListingActive(listingId, false)
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    _isActive.value = false
                    _success.value = true
                },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun clearError() { _error.value = null }
}
