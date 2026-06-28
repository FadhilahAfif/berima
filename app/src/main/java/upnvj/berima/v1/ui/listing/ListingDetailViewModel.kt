package upnvj.berima.v1.ui.listing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.data.model.Review
import upnvj.berima.v1.data.model.User
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.ListingRepository
import upnvj.berima.v1.data.repository.ReviewRepository
import upnvj.berima.v1.navigation.Screen
import upnvj.berima.v1.ui.common.AppStrings
import javax.inject.Inject

@HiltViewModel
class ListingDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listingRepository: ListingRepository,
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val listingId: String =
        checkNotNull(savedStateHandle[Screen.ListingDetail.ARG_LISTING_ID])

    private val _listing = MutableStateFlow<Listing?>(null)
    val listing: StateFlow<Listing?> = _listing.asStateFlow()

    private val _seller = MutableStateFlow<User?>(null)
    val seller: StateFlow<User?> = _seller.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    val reviews: StateFlow<List<Review>> = reviewRepository
        .getReviewsForListing(listingId)
        .catch { e -> _error.value = e.message }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val currentUserId: String? get() = auth.currentUser?.uid
    private var sellerJob: Job? = null

    init {
        loadListing()
    }

    private fun loadListing() {
        viewModelScope.launch {
            _isLoading.value = true
            listingRepository.observeListing(listingId)
                .catch { e -> _error.value = e.message }
                .collect { listing ->
                    _listing.value = listing
                    observeSeller(listing?.sellerId)
                    _isLoading.value = false
                }
        }
    }

    private fun observeSeller(sellerId: String?) {
        if (sellerId.isNullOrBlank() || sellerJob?.isActive == true && _seller.value?.uid == sellerId) {
            return
        }
        sellerJob?.cancel()
        sellerJob = viewModelScope.launch {
            authRepository.observeUser(sellerId)
                .catch { e -> _error.value = e.message }
                .collect { user -> _seller.value = user }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun deactivateListing() {
        viewModelScope.launch {
            val result = listingRepository.setListingActive(listingId, false)
            result.fold(
                onSuccess = { _success.value = AppStrings.LISTING_DEACTIVATE_SUCCESS },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun clearSuccess() {
        _success.value = null
    }
}
