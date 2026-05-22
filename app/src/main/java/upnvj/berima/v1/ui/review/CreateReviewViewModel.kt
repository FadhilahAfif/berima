package upnvj.berima.v1.ui.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Order
import upnvj.berima.v1.data.model.Review
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.OrderRepository
import upnvj.berima.v1.data.repository.ReviewRepository
import upnvj.berima.v1.navigation.Screen
import javax.inject.Inject

@HiltViewModel
class CreateReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val orderId: String =
        checkNotNull(savedStateHandle[Screen.CreateReview.ARG_ORDER_ID])

    private val _order = MutableStateFlow<Order?>(null)
    val order: StateFlow<Order?> = _order.asStateFlow()

    private val _rating = MutableStateFlow(0)
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted: StateFlow<Boolean> = _isSubmitted.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            _isLoading.value = true
            orderRepository.getOrder(orderId).fold(
                onSuccess = { _order.value = it },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun onRatingChange(v: Int) {
        _rating.value = v
    }

    fun onCommentChange(v: String) {
        if (v.length <= Validation.MAX_REVIEW_COMMENT_LENGTH) {
            _comment.value = v
        }
    }

    fun submit() {
        if (_isLoading.value) return
        val o = _order.value ?: run {
            _error.value = "Pesanan belum siap"
            return
        }
        if (_rating.value == 0) {
            _error.value = "Pilih rating terlebih dahulu"
            return
        }
        val uid = authRepository.currentUserId ?: run {
            _error.value = "Sesi berakhir, silakan login ulang"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val review = Review(
                orderId = o.orderId,
                listingId = o.listingId,
                buyerId = uid,
                buyerName = o.buyerName,
                sellerId = o.sellerId,
                rating = _rating.value,
                comment = _comment.value.trim().ifBlank { null },
                createdAt = Timestamp.now()
            )
            reviewRepository.createReview(review).fold(
                onSuccess = { _isSubmitted.value = true },
                onFailure = { _error.value = it.message ?: "Gagal mengirim ulasan" }
            )
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
