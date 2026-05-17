package upnvj.berima.v1.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.repository.AuthRepository
import javax.inject.Inject

sealed interface SplashDestination {
    data object Idle : SplashDestination
    data object Home : SplashDestination
    data object Login : SplashDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Idle)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1800)
            _destination.value = if (authRepository.isLoggedIn()) {
                SplashDestination.Home
            } else {
                SplashDestination.Login
            }
        }
    }
}
