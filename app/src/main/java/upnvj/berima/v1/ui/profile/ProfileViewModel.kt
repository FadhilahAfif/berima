package upnvj.berima.v1.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.data.model.PortfolioItem
import upnvj.berima.v1.data.model.User
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.ListingRepository
import upnvj.berima.v1.data.repository.PortfolioRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val listingRepository: ListingRepository,
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    val currentUserId: String? = authRepository.currentUserId

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    val listings: StateFlow<List<Listing>> = _listings.asStateFlow()

    private val _portfolioItems = MutableStateFlow<List<PortfolioItem>>(emptyList())
    val portfolioItems: StateFlow<List<PortfolioItem>> = _portfolioItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val uid = currentUserId
        if (uid != null) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    authRepository.observeUser(uid).collect { _user.value = it }
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _isLoading.value = false
                }
            }
            viewModelScope.launch {
                try {
                    listingRepository.getListingsBySeller(uid).collect { _listings.value = it }
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
            viewModelScope.launch {
                try {
                    portfolioRepository.observePortfolioItems(uid).collect { _portfolioItems.value = it }
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun clearError() {
        _error.value = null
    }
}
