package upnvj.berima.v1.ui.listing

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Category
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.ListingRepository
import upnvj.berima.v1.data.repository.StorageRepository
import upnvj.berima.v1.ui.common.AppStrings
import javax.inject.Inject

@HiltViewModel
class CreateListingViewModel @Inject constructor(
    private val listingRepository: ListingRepository,
    private val storageRepository: StorageRepository,
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _category = MutableStateFlow(Category.ACADEMIC)
    val category: StateFlow<String> = _category.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price.asStateFlow()

    private val _deliveryTimeHours = MutableStateFlow("")
    val deliveryTimeHours: StateFlow<String> = _deliveryTimeHours.asStateFlow()

    private val _tags = MutableStateFlow("")
    val tags: StateFlow<String> = _tags.asStateFlow()

    private val _selectedThumbnailUri = MutableStateFlow<Uri?>(null)
    val selectedThumbnailUri: StateFlow<Uri?> = _selectedThumbnailUri.asStateFlow()

    private val _isPolicyAccepted = MutableStateFlow(false)
    val isPolicyAccepted: StateFlow<Boolean> = _isPolicyAccepted.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

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
    }

    fun removeThumbnail() {
        _selectedThumbnailUri.value = null
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

        val uid = auth.currentUser?.uid ?: run {
            _error.value = "Sesi berakhir, silakan login ulang"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val userResult = authRepository.getUser(uid)
            val user = userResult.getOrNull()
            val listingId = listingRepository.newListingId()
            val thumbnailUrl = _selectedThumbnailUri.value?.let { uri ->
                val uploadResult = storageRepository.uploadListingThumbnail(uid, listingId, uri)
                uploadResult.getOrElse {
                    _isLoading.value = false
                    _error.value = it.message ?: "Gagal mengunggah gambar listing"
                    return@launch
                }.downloadUrl
            }

            val tagList = _tags.value
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            val listing = Listing(
                sellerId = uid,
                sellerName = user?.name ?: "",
                sellerPhotoUrl = user?.photoUrl,
                sellerRating = user?.averageRating ?: 0.0,
                sellerIdentityVerified = user?.isIdentityVerified ?: false,
                sellerVerifiedSkillBadges = user?.verifiedSkillBadges.orEmpty(),
                title = titleVal,
                description = descVal,
                category = _category.value,
                price = priceVal,
                deliveryTimeHours = deliveryVal,
                thumbnailUrl = thumbnailUrl,
                tags = tagList,
                isActive = true,
                policyAcceptedAt = Timestamp.now(),
                createdAt = Timestamp.now()
            )

            val result = listingRepository.createListing(listing, listingId)
            _isLoading.value = false
            result.fold(
                onSuccess = { _success.value = true },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun clearError() { _error.value = null }
}
