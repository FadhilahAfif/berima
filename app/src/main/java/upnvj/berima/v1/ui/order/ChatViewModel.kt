package upnvj.berima.v1.ui.order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Message
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.MessageRepository
import upnvj.berima.v1.navigation.Screen
import javax.inject.Inject

/**
 * Streams chat messages for a single order and exposes a draft + send
 * interface. Lives alongside [OrderDetailViewModel] on the same screen
 * so that order doc errors and chat errors stay on separate channels.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val messageRepository: MessageRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val orderId: String =
        checkNotNull(savedStateHandle[Screen.OrderDetail.ARG_ORDER_ID])

    val currentUserId: String? = authRepository.currentUserId

    private val _draft = MutableStateFlow("")
    val draft: StateFlow<String> = _draft.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val messages: StateFlow<List<Message>> = messageRepository
        .observeMessages(orderId)
        .catch { e -> _error.value = e.message ?: "Gagal memuat pesan" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onDraftChange(text: String) {
        if (text.length <= Validation.MAX_MESSAGE_LENGTH) {
            _draft.value = text
        }
    }

    fun send(senderName: String) {
        val text = _draft.value.trim()
        if (text.isEmpty() || _isSending.value) return
        val uid = currentUserId ?: run {
            _error.value = "Sesi berakhir, silakan login ulang"
            return
        }
        viewModelScope.launch {
            _isSending.value = true
            messageRepository.sendMessage(orderId, uid, senderName, text)
                .onSuccess { _draft.value = "" }
                .onFailure {
                    _error.value = it.message ?: "Gagal mengirim pesan"
                }
            _isSending.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
