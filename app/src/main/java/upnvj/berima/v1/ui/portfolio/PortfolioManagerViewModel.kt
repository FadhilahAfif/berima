package upnvj.berima.v1.ui.portfolio

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upnvj.berima.v1.data.model.Category
import upnvj.berima.v1.data.model.PortfolioItem
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.PortfolioRepository
import upnvj.berima.v1.data.repository.StorageRepository
import upnvj.berima.v1.ui.common.AppStrings
import javax.inject.Inject

@HiltViewModel
class PortfolioManagerViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val portfolioRepository: PortfolioRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<PortfolioItem>>(emptyList())
    val items: StateFlow<List<PortfolioItem>> = _items.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _category = MutableStateFlow(Category.ACADEMIC)
    val category: StateFlow<String> = _category.asStateFlow()

    private val _externalLink = MutableStateFlow("")
    val externalLink: StateFlow<String> = _externalLink.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _selectedImageName = MutableStateFlow<String?>(null)
    val selectedImageName: StateFlow<String?> = _selectedImageName.asStateFlow()

    private val _editingItem = MutableStateFlow<PortfolioItem?>(null)
    val editingItem: StateFlow<PortfolioItem?> = _editingItem.asStateFlow()

    private val _removeExistingImage = MutableStateFlow(false)
    val removeExistingImage: StateFlow<Boolean> = _removeExistingImage.asStateFlow()

    private val _isFormOpen = MutableStateFlow(false)
    val isFormOpen: StateFlow<Boolean> = _isFormOpen.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    init {
        val uid = authRepository.currentUserId
        if (uid == null) {
            _error.value = AppStrings.PORTFOLIO_ERROR_LOGIN_REQUIRED
            _isLoading.value = false
        } else {
            observePortfolio(uid)
        }
    }

    fun openCreateForm() {
        resetForm()
        _isFormOpen.value = true
    }

    fun openEditForm(item: PortfolioItem) {
        _editingItem.value = item
        _title.value = item.title
        _description.value = item.description
        _category.value = item.category
        _externalLink.value = item.externalLink.orEmpty()
        _selectedImageUri.value = null
        _selectedImageName.value = null
        _removeExistingImage.value = false
        _isFormOpen.value = true
    }

    fun closeForm() {
        resetForm()
    }

    fun onTitleChange(value: String) {
        if (value.length <= Validation.MAX_PORTFOLIO_TITLE_LENGTH) _title.value = value
    }

    fun onDescriptionChange(value: String) {
        if (value.length <= Validation.MAX_PORTFOLIO_DESCRIPTION_LENGTH) _description.value = value
    }

    fun onCategoryChange(value: String) {
        if (value in Category.ALL) _category.value = value
    }

    fun onExternalLinkChange(value: String) {
        _externalLink.value = value.take(200)
    }

    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
        _selectedImageName.value = uri?.lastPathSegment?.substringAfterLast('/')
        if (uri != null) _removeExistingImage.value = false
    }

    fun removeCurrentImage() {
        _selectedImageUri.value = null
        _selectedImageName.value = null
        _removeExistingImage.value = true
    }

    fun save() {
        val uid = authRepository.currentUserId
        val titleValue = _title.value.trim()
        val descriptionValue = _description.value.trim()
        val link = _externalLink.value.trim()
        val imageUri = _selectedImageUri.value
        val editing = _editingItem.value

        when {
            uid == null -> {
                _error.value = AppStrings.PORTFOLIO_ERROR_LOGIN_REQUIRED
                return
            }
            titleValue.isBlank() -> {
                _error.value = AppStrings.PORTFOLIO_ERROR_TITLE_REQUIRED
                return
            }
            descriptionValue.isBlank() -> {
                _error.value = AppStrings.PORTFOLIO_ERROR_DESCRIPTION_REQUIRED
                return
            }
            link.isNotBlank() && !link.startsWith("http://") && !link.startsWith("https://") -> {
                _error.value = AppStrings.PORTFOLIO_ERROR_LINK_INVALID
                return
            }
        }

        viewModelScope.launch {
            _isSaving.value = true
            var uploadedPath: String? = null
            try {
                val itemId = editing?.portfolioItemId ?: portfolioRepository.newPortfolioItemId()
                val uploaded = imageUri?.let {
                    storageRepository.uploadPortfolioImage(uid, itemId, it)
                        .getOrElse { error -> throw error }
                }
                uploadedPath = uploaded?.storagePath

                if (editing == null) {
                    portfolioRepository.createPortfolioItem(
                        itemId = itemId,
                        userId = uid,
                        title = titleValue,
                        description = descriptionValue,
                        category = _category.value,
                        externalLink = link,
                        imageUrl = uploaded?.downloadUrl,
                        imageStoragePath = uploaded?.storagePath
                    ).getOrElse { throw it }
                    _success.value = AppStrings.PORTFOLIO_CREATE_SUCCESS
                } else {
                    val oldPath = editing.imageStoragePath
                    val removeImage = _removeExistingImage.value
                    portfolioRepository.updatePortfolioItem(
                        itemId = editing.portfolioItemId,
                        title = titleValue,
                        description = descriptionValue,
                        category = _category.value,
                        externalLink = link,
                        imageUrl = if (removeImage) null else uploaded?.downloadUrl ?: editing.imageUrl,
                        imageStoragePath = if (removeImage) null else uploaded?.storagePath ?: editing.imageStoragePath,
                        removeImage = removeImage || uploaded != null
                    ).getOrElse { throw it }
                    if ((removeImage || uploaded != null) && !oldPath.isNullOrBlank()) {
                        storageRepository.deleteFile(oldPath)
                    }
                    _success.value = AppStrings.PORTFOLIO_UPDATE_SUCCESS
                }
                resetForm()
            } catch (e: Exception) {
                uploadedPath?.let { storageRepository.deleteFile(it) }
                _error.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun delete(item: PortfolioItem) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                portfolioRepository.deletePortfolioItem(item.portfolioItemId)
                    .getOrElse { throw it }
                item.imageStoragePath?.takeIf { it.isNotBlank() }?.let {
                    storageRepository.deleteFile(it)
                }
                if (_editingItem.value?.portfolioItemId == item.portfolioItemId) {
                    resetForm()
                }
                _success.value = AppStrings.PORTFOLIO_DELETE_SUCCESS
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }

    private fun observePortfolio(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                portfolioRepository.observePortfolioItems(uid).collect { portfolioItems ->
                    _items.value = portfolioItems
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    private fun resetForm() {
        _title.value = ""
        _description.value = ""
        _category.value = Category.ACADEMIC
        _externalLink.value = ""
        _selectedImageUri.value = null
        _selectedImageName.value = null
        _editingItem.value = null
        _removeExistingImage.value = false
        _isFormOpen.value = false
    }
}
