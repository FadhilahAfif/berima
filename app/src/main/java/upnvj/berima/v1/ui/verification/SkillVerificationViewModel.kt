package upnvj.berima.v1.ui.verification

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
import upnvj.berima.v1.data.model.User
import upnvj.berima.v1.data.model.VerificationStatus
import upnvj.berima.v1.data.model.VerificationSubmission
import upnvj.berima.v1.data.model.VerificationType
import upnvj.berima.v1.data.repository.AuthRepository
import upnvj.berima.v1.data.repository.StorageRepository
import upnvj.berima.v1.data.repository.VerificationRepository
import upnvj.berima.v1.ui.common.AppStrings
import javax.inject.Inject

@HiltViewModel
class SkillVerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val verificationRepository: VerificationRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _submissions = MutableStateFlow<List<VerificationSubmission>>(emptyList())
    val submissions: StateFlow<List<VerificationSubmission>> = _submissions.asStateFlow()

    private val _portfolioItems = MutableStateFlow<List<PortfolioItem>>(emptyList())
    val portfolioItems: StateFlow<List<PortfolioItem>> = _portfolioItems.asStateFlow()

    private val _selectedCategory = MutableStateFlow(Category.ACADEMIC)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedPortfolioItemId = MutableStateFlow<String?>(null)
    val selectedPortfolioItemId: StateFlow<String?> = _selectedPortfolioItemId.asStateFlow()

    private val _externalLink = MutableStateFlow("")
    val externalLink: StateFlow<String> = _externalLink.asStateFlow()

    private val _selectedUri = MutableStateFlow<Uri?>(null)
    val selectedUri: StateFlow<Uri?> = _selectedUri.asStateFlow()

    private val _selectedFileName = MutableStateFlow<String?>(null)
    val selectedFileName: StateFlow<String?> = _selectedFileName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    init {
        val uid = authRepository.currentUserId
        if (uid != null) {
            observeUser(uid)
            observeSubmissions(uid)
            observePortfolio(uid)
        }
    }

    fun onCategorySelected(category: String) {
        if (category in Category.ALL) {
            _selectedCategory.value = category
            val currentPortfolio = _selectedPortfolioItemId.value
            if (currentPortfolio != null && _portfolioItems.value.none {
                    it.portfolioItemId == currentPortfolio && it.category == category
                }
            ) {
                _selectedPortfolioItemId.value = null
            }
        }
    }

    fun onPortfolioSelected(portfolioItemId: String?) {
        _selectedPortfolioItemId.value = portfolioItemId
    }

    fun onExternalLinkChange(value: String) {
        _externalLink.value = value.take(200)
    }

    fun onFileSelected(uri: Uri?) {
        _selectedUri.value = uri
        _selectedFileName.value = uri?.lastPathSegment?.substringAfterLast('/')
    }

    fun submit() {
        val uid = authRepository.currentUserId
        val category = _selectedCategory.value
        val existing = latestSubmissionFor(category)
        val user = _user.value
        val link = _externalLink.value.trim()
        val portfolioId = _selectedPortfolioItemId.value
        val uri = _selectedUri.value

        when {
            uid == null -> {
                _error.value = AppStrings.VERIFICATION_ERROR_LOGIN_REQUIRED
                return
            }
            user?.verifiedSkillBadges.orEmpty().contains(category) -> {
                _error.value = AppStrings.VERIFICATION_ERROR_ALREADY_APPROVED
                return
            }
            existing?.status == VerificationStatus.PENDING -> {
                _error.value = AppStrings.VERIFICATION_ERROR_PENDING_DUPLICATE
                return
            }
            existing?.status == VerificationStatus.APPROVED -> {
                _error.value = AppStrings.VERIFICATION_ERROR_ALREADY_APPROVED
                return
            }
            link.isNotBlank() && !link.startsWith("http://") && !link.startsWith("https://") -> {
                _error.value = AppStrings.SKILL_ERROR_LINK_INVALID
                return
            }
            portfolioId == null && link.isBlank() && uri == null -> {
                _error.value = AppStrings.SKILL_ERROR_EVIDENCE_REQUIRED
                return
            }
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            var uploadedPath: String? = null
            try {
                val submissionId = verificationRepository.newSubmissionId()
                val metadata = uri?.let {
                    storageRepository.uploadSkillEvidence(uid, submissionId, it)
                        .getOrElse { error -> throw error }
                }
                uploadedPath = metadata?.storagePath
                verificationRepository.createSkillSubmission(
                    submissionId = submissionId,
                    userId = uid,
                    category = category,
                    portfolioItemId = portfolioId,
                    externalLink = link,
                    storagePath = metadata?.storagePath,
                    fileName = metadata?.fileName,
                    contentType = metadata?.contentType
                ).getOrElse { throw it }

                _selectedPortfolioItemId.value = null
                _externalLink.value = ""
                _selectedUri.value = null
                _selectedFileName.value = null
                _success.value = AppStrings.SKILL_SUBMIT_SUCCESS
            } catch (e: Exception) {
                uploadedPath?.let { storageRepository.deleteFile(it) }
                _error.value = e.message
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun latestSubmissionFor(category: String): VerificationSubmission? {
        return _submissions.value.firstOrNull { it.skillCategory == category }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }

    private fun observeUser(uid: String) {
        viewModelScope.launch {
            try {
                authRepository.observeUser(uid).collect { _user.value = it }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun observeSubmissions(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                verificationRepository
                    .observeSubmissions(uid, VerificationType.SKILL)
                    .collect { submissions ->
                        _submissions.value = submissions
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    private fun observePortfolio(uid: String) {
        viewModelScope.launch {
            try {
                verificationRepository.observePortfolioItems(uid).collect { items ->
                    _portfolioItems.value = items
                    val current = _selectedPortfolioItemId.value
                    if (current != null && items.none { it.portfolioItemId == current }) {
                        _selectedPortfolioItemId.value = null
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
