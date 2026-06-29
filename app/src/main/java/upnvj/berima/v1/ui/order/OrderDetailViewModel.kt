package upnvj.berima.v1.ui.order

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Order
import upnvj.berima.v1.data.model.OrderStatus
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.OrderRepository
import upnvj.berima.v1.data.repository.StorageRepository
import upnvj.berima.v1.navigation.Screen
import javax.inject.Inject

/**
 * Owns the order document stream and the action dispatch matrix.
 *
 * Action execution is centralized through [onAction] — the composable
 * decides which buttons to render based on `(order.status, isBuyer)`,
 * but the actual repo calls all flow through here so the role × status
 * matrix lives in one place.
 */
@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository,
    private val storageRepository: StorageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val orderId: String =
        checkNotNull(savedStateHandle[Screen.OrderDetail.ARG_ORDER_ID])

    val currentUserId: String? = authRepository.currentUserId

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _actionInFlight = MutableStateFlow<String?>(null)
    val actionInFlight: StateFlow<String?> = _actionInFlight.asStateFlow()

    private val _navigateToReview = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navigateToReview: SharedFlow<String> = _navigateToReview.asSharedFlow()

    val order: StateFlow<Order?> = orderRepository
        .observeOrder(orderId)
        .onEach { _isLoading.value = false }
        .catch { e ->
            _error.value = e.message ?: "Gagal memuat pesanan"
            _isLoading.value = false
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val isBuyer: StateFlow<Boolean> = order
        .map { it != null && it.buyerId == currentUserId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    /**
     * Single action dispatch. Sets [actionInFlight] for the duration of the
     * call, surfaces any failure on [error], always clears the in-flight
     * flag in a finally block.
     */
    fun onAction(action: OrderAction) {
        if (_actionInFlight.value != null) return
        viewModelScope.launch {
            _actionInFlight.value = action::class.simpleName ?: "Action"
            try {
                val result: Result<Unit> = when (action) {
                    OrderAction.Accept ->
                        requireUser { orderRepository.acceptOrder(orderId, it) }

                    OrderAction.Reject ->
                        requireUser { orderRepository.rejectOrder(orderId, it) }

                    OrderAction.Cancel ->
                        requireUser { orderRepository.cancelOrder(orderId, it) }

                    is OrderAction.UploadResult -> uploadResult(action.uri)

                    is OrderAction.RequestRevision ->
                        requireUser { orderRepository.requestRevision(orderId, it, action.note.trim()) }

                    OrderAction.ConfirmDelivered ->
                        requireUser { orderRepository.confirmDelivered(orderId, it) }

                    OrderAction.SimulatePay ->
                        requireUser { orderRepository.markPaid(orderId, it) }

                    OrderAction.OpenReview -> {
                        _navigateToReview.emit(orderId)
                        Result.success(Unit)
                    }
                }
                result.onFailure {
                    _error.value = it.message ?: "Terjadi kesalahan"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _actionInFlight.value = null
            }
        }
    }

    private suspend fun uploadResult(uri: Uri): Result<Unit> {
        return storageRepository.uploadOrderResult(orderId, uri).fold(
            onSuccess = { metadata ->
                requireUser { orderRepository.setResultFile(orderId, it, metadata) }
            },
            onFailure = { Result.failure(it) }
        )
    }

    private suspend fun requireUser(block: suspend (String) -> Result<Unit>): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(
            IllegalStateException("Sesi berakhir, silakan login ulang")
        )
        return block(uid)
    }

    fun clearError() {
        _error.value = null
    }
}
