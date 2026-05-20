package upnvj.berima.v1.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import upnvj.berima.v1.data.model.Order
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.OrderRepository
import javax.inject.Inject

/**
 * Hosts the two parallel order streams for the Orders bottom-nav tab.
 * Tab 0 = "Sebagai Pembeli" (orders the user placed),
 * tab 1 = "Sebagai Penjual" (orders against the user's listings).
 *
 * The current user id is read once at VM construction. Identity can only
 * change via Login/Logout, both of which tear this VM down.
 */
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val currentUserId: String? = authRepository.currentUserId

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val buyerOrders: StateFlow<List<Order>> = sourceOrError {
        orderRepository.getOrdersAsBuyer(it)
    }

    val sellerOrders: StateFlow<List<Order>> = sourceOrError {
        orderRepository.getOrdersAsSeller(it)
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun clearError() {
        _error.value = null
    }

    /**
     * Wraps a repo flow with the project's standard error handling and
     * collapses to an empty StateFlow when no user is signed in.
     */
    private fun sourceOrError(
        builder: (uid: String) -> Flow<List<Order>>
    ): StateFlow<List<Order>> {
        val source: Flow<List<Order>> = currentUserId
            ?.let(builder)
            ?: MutableStateFlow(emptyList())
        return source
            .catch { e -> _error.value = e.message }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    }
}
