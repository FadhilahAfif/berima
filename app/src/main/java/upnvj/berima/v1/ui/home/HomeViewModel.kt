package upnvj.berima.v1.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.ListingRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val listingRepository: ListingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** First name of the logged-in user, for the home greeting. Empty until loaded. */
    val userFirstName: StateFlow<String> = run {
        val uid = authRepository.currentUserId
        if (uid == null) {
            MutableStateFlow("")
        } else {
            authRepository.observeUser(uid)
                .map { it?.name?.trim()?.substringBefore(' ').orEmpty() }
                .catch { emit("") }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")
        }
    }

    // Independent loading flags per query so one resolving early can't blank out the
    // other section. The main list owns the "Terbaru" loading state.
    private val _featuredLoading = MutableStateFlow(true)
    private val _listLoading = MutableStateFlow(true)
    val isListLoading: StateFlow<Boolean> = _listLoading.asStateFlow()

    val featuredListings: StateFlow<List<Listing>> = listingRepository
        .getFeaturedListings(limit = 6L)
        .onStart { _featuredLoading.value = true }
        .onEach { _featuredLoading.value = false }
        .catch { e -> _error.value = e.message; _featuredLoading.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val listings: StateFlow<List<Listing>> = _selectedCategory
        .flatMapLatest { category ->
            if (category == null) {
                listingRepository.getActiveListings(limit = 20L)
            } else {
                listingRepository.getListingsByCategory(category, limit = 20L)
            }
                .onStart { _listLoading.value = true }
                .onEach { _listLoading.value = false }
                .catch { e -> _error.value = e.message; _listLoading.value = false }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun clearError() {
        _error.value = null
    }
}
