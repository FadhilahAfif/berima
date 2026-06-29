package upnvj.berima.v1.ui.order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.data.model.Order
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.ListingRepository
import upnvj.berima.v1.data.repository.OrderRepository
import upnvj.berima.v1.navigation.Screen
import javax.inject.Inject

/**
 * Loads the listing being ordered and submits a new [Order] in `pending`
 * state. The buyer's display name is denormalized onto the order so the
 * seller's order list doesn't need a follow-up read.
 */
@HiltViewModel
class CreateOrderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listingRepository: ListingRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val listingId: String =
        checkNotNull(savedStateHandle[Screen.CreateOrder.ARG_LISTING_ID])

    private val _listing = MutableStateFlow<Listing?>(null)
    val listing: StateFlow<Listing?> = _listing.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _createdOrderId = MutableStateFlow<String?>(null)
    val createdOrderId: StateFlow<String?> = _createdOrderId.asStateFlow()

    init {
        loadListing()
    }

    private fun loadListing() {
        viewModelScope.launch {
            _isLoading.value = true
            listingRepository.getListing(listingId).fold(
                onSuccess = { _listing.value = it },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun onNoteChange(value: String) {
        if (value.length <= Validation.MAX_ORDER_NOTE_LENGTH) {
            _note.value = value
        }
    }

    fun submit() {
        if (_isSubmitting.value) return

        val listing = _listing.value ?: run {
            _error.value = "Layanan belum siap"
            return
        }
        val uid = authRepository.currentUserId ?: run {
            _error.value = "Sesi berakhir, silakan login ulang"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            val buyer = authRepository.getUser(uid).getOrNull()

            val order = Order(
                listingId = listing.listingId,
                listingTitle = listing.title,
                buyerId = uid,
                buyerName = buyer?.name ?: "",
                sellerId = listing.sellerId,
                sellerName = listing.sellerName,
                price = listing.price,
                note = _note.value.trim().ifBlank { null }
            )

            orderRepository.createOrder(order).fold(
                onSuccess = { orderId -> _createdOrderId.value = orderId },
                onFailure = { _error.value = it.message ?: "Gagal membuat pesanan" }
            )
            _isSubmitting.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
