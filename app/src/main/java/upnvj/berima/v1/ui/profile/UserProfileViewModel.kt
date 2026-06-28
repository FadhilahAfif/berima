package upnvj.berima.v1.ui.profile

import androidx.lifecycle.SavedStateHandle
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
import upnvj.berima.v1.navigation.Screen
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val listingRepository: ListingRepository,
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle[Screen.UserProfile.ARG_USER_ID])

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    val listings: StateFlow<List<Listing>> = _listings.asStateFlow()

    private val _portfolioItems = MutableStateFlow<List<PortfolioItem>>(emptyList())
    val portfolioItems: StateFlow<List<PortfolioItem>> = _portfolioItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.getUser(userId)
            result.fold(
                onSuccess = { _user.value = it },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
        viewModelScope.launch {
            try {
                listingRepository.getListingsBySeller(userId).collect { all ->
                    _listings.value = all.filter { it.isActive }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
        viewModelScope.launch {
            try {
                portfolioRepository.observePortfolioItems(userId).collect { items ->
                    _portfolioItems.value = items
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
