package upnvj.berima.v1.ui.listing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.data.repository.ListingRepository
import upnvj.berima.v1.navigation.Screen
import javax.inject.Inject

@HiltViewModel
class EditListingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listingRepository: ListingRepository
) : ViewModel() {

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

        val tagList = _tags.value
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        viewModelScope.launch {
            _isLoading.value = true
            val result = listingRepository.updateListing(
                listingId = listingId,
                title = titleVal,
                description = descVal,
                category = _category.value,
                price = priceVal,
                deliveryTimeHours = deliveryVal,
                tags = tagList,
                thumbnailUrl = null
            )
            _isLoading.value = false
            result.fold(
                onSuccess = { _success.value = true },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun clearError() { _error.value = null }
}
