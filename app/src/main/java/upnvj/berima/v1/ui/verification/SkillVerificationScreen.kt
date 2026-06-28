package upnvj.berima.v1.ui.verification

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Category
import upnvj.berima.v1.data.model.PortfolioItem
import upnvj.berima.v1.data.model.User
import upnvj.berima.v1.data.model.VerificationStatus
import upnvj.berima.v1.data.model.VerificationSubmission
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
import upnvj.berima.v1.ui.common.CategoryPickerField
import upnvj.berima.v1.ui.common.categoryFullLabel
import upnvj.berima.v1.ui.theme.BerimaTheme
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import upnvj.berima.v1.ui.theme.StatusColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillVerificationScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SkillVerificationViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val submissions by viewModel.submissions.collectAsStateWithLifecycle()
    val portfolioItems by viewModel.portfolioItems.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedPortfolioItemId by viewModel.selectedPortfolioItemId.collectAsStateWithLifecycle()
    val externalLink by viewModel.externalLink.collectAsStateWithLifecycle()
    val selectedUri by viewModel.selectedUri.collectAsStateWithLifecycle()
    val selectedFileName by viewModel.selectedFileName.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val berimaColors = LocalBerimaColors.current
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onFileSelected(uri)
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    LaunchedEffect(success) {
        success?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.SKILL_TITLE,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = AppStrings.BACK_CONTENT_DESCRIPTION,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = berimaColors.surfaceRaised
                )
            )
        },
        modifier = modifier
    ) { padding ->
        if (isLoading && submissions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            SkillVerificationContent(
                user = user,
                submissions = submissions,
                portfolioItems = portfolioItems,
                selectedCategory = selectedCategory,
                selectedPortfolioItemId = selectedPortfolioItemId,
                externalLink = externalLink,
                selectedUri = selectedUri,
                selectedFileName = selectedFileName,
                isSubmitting = isSubmitting,
                onCategorySelected = viewModel::onCategorySelected,
                onPortfolioSelected = viewModel::onPortfolioSelected,
                onExternalLinkChange = viewModel::onExternalLinkChange,
                onPickFile = { filePicker.launch("*/*") },
                onSubmit = viewModel::submit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}

@Composable
private fun SkillVerificationContent(
    user: User?,
    submissions: List<VerificationSubmission>,
    portfolioItems: List<PortfolioItem>,
    selectedCategory: String,
    selectedPortfolioItemId: String?,
    externalLink: String,
    selectedUri: Uri?,
    selectedFileName: String?,
    isSubmitting: Boolean,
    onCategorySelected: (String) -> Unit,
    onPortfolioSelected: (String?) -> Unit,
    onExternalLinkChange: (String) -> Unit,
    onPickFile: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val submission = submissions.firstOrNull { it.skillCategory == selectedCategory }
    val isPublicBadgeApproved = user?.verifiedSkillBadges.orEmpty().contains(selectedCategory)
    val status = if (isPublicBadgeApproved) {
        VerificationStatus.APPROVED
    } else {
        submission?.status ?: VerificationStatus.NOT_SUBMITTED
    }
    val canSubmit = status == VerificationStatus.NOT_SUBMITTED ||
        status == VerificationStatus.REJECTED
    val filteredPortfolio = portfolioItems.filter { it.category == selectedCategory }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SkillStatusCard(
            category = selectedCategory,
            status = status,
            rejectionReason = submission?.rejectionReason
        )
        Spacer(Modifier.height(16.dp))
        CategoryPickerField(
            selected = selectedCategory,
            onSelected = onCategorySelected,
            label = AppStrings.SKILL_SECTION_CATEGORY
        )
        Spacer(Modifier.height(16.dp))

        if (canSubmit) {
            PortfolioPickerCard(
                items = filteredPortfolio,
                selectedPortfolioItemId = selectedPortfolioItemId,
                onSelected = onPortfolioSelected
            )
            Spacer(Modifier.height(16.dp))
            BerimaTextField(
                value = externalLink,
                onValueChange = onExternalLinkChange,
                label = AppStrings.SKILL_LINK_LABEL,
                placeholder = AppStrings.SKILL_LINK_PLACEHOLDER
            )
            Spacer(Modifier.height(16.dp))
            SkillFilePickerCard(
                selectedFileName = selectedFileName ?: selectedUri?.lastPathSegment,
                pickLabel = if (selectedUri == null) {
                    AppStrings.SKILL_PICK_FILE
                } else {
                    AppStrings.SKILL_CHANGE_FILE
                },
                onPickFile = onPickFile
            )
            Spacer(Modifier.height(20.dp))
            BerimaButton(
                text = AppStrings.SKILL_SUBMIT,
                onClick = onSubmit,
                isLoading = isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SkillStatusCard(
    category: String,
    status: String,
    rejectionReason: String?,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, shape)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(berimaColors.containerGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(21.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoryFullLabel(category),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                VerificationStatusPill(status = status)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = AppStrings.SKILL_STATUS_BODY,
            style = MaterialTheme.typography.bodyMedium,
            color = berimaColors.textSecondary
        )
        if (status == VerificationStatus.REJECTED) {
            Spacer(Modifier.height(12.dp))
            RejectionCallout(reason = rejectionReason)
        }
    }
}

@Composable
private fun PortfolioPickerCard(
    items: List<PortfolioItem>,
    selectedPortfolioItemId: String?,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, shape)
            .padding(16.dp)
    ) {
        Text(
            text = AppStrings.SKILL_SECTION_PORTFOLIO,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(10.dp))
        PortfolioOptionRow(
            title = AppStrings.SKILL_PORTFOLIO_NONE,
            body = null,
            isSelected = selectedPortfolioItemId == null,
            onClick = { onSelected(null) }
        )
        if (items.isEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = AppStrings.SKILL_PORTFOLIO_EMPTY,
                style = MaterialTheme.typography.bodyMedium,
                color = berimaColors.textSecondary
            )
        } else {
            items.forEach { item ->
                Spacer(Modifier.height(10.dp))
                PortfolioOptionRow(
                    title = item.title,
                    body = item.description,
                    isSelected = selectedPortfolioItemId == item.portfolioItemId,
                    onClick = { onSelected(item.portfolioItemId) }
                )
            }
        }
    }
}

@Composable
private fun PortfolioOptionRow(
    title: String,
    body: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else berimaColors.borderSubtle,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            body?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = berimaColors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (isSelected) {
            Spacer(Modifier.width(12.dp))
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SkillFilePickerCard(
    selectedFileName: String?,
    pickLabel: String,
    onPickFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, shape)
            .clickable(onClick = onPickFile)
            .padding(16.dp)
    ) {
        Text(
            text = AppStrings.SKILL_FILE_TITLE,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = AppStrings.SKILL_FILE_BODY,
            style = MaterialTheme.typography.bodyMedium,
            color = berimaColors.textSecondary
        )
        selectedFileName?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(14.dp))
            Text(
                text = AppStrings.IDENTITY_SELECTED_FILE,
                style = MaterialTheme.typography.labelSmall,
                color = berimaColors.textSecondary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = pickLabel,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun VerificationStatusPill(status: String, modifier: Modifier = Modifier) {
    val colors = verificationStatusColors(status)
    Text(
        text = verificationStatusLabel(status).uppercase(Locale("id", "ID")),
        style = MaterialTheme.typography.labelSmall,
        color = colors.text,
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(colors.container)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun RejectionCallout(reason: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(12.dp)
    ) {
        Text(
            text = AppStrings.VERIFICATION_REJECTION_LABEL,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = reason?.takeIf { it.isNotBlank() }
                ?: AppStrings.VERIFICATION_REJECTION_FALLBACK,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
private fun verificationStatusColors(status: String): StatusColors {
    val berimaColors = LocalBerimaColors.current
    return when (status) {
        VerificationStatus.PENDING -> berimaColors.statusPending
        VerificationStatus.APPROVED -> berimaColors.statusCompleted
        VerificationStatus.REJECTED -> berimaColors.statusRejected
        else -> berimaColors.statusCancelled
    }
}

private fun verificationStatusLabel(status: String): String = when (status) {
    VerificationStatus.PENDING -> AppStrings.VERIFICATION_STATUS_PENDING
    VerificationStatus.APPROVED -> AppStrings.VERIFICATION_STATUS_APPROVED
    VerificationStatus.REJECTED -> AppStrings.VERIFICATION_STATUS_REJECTED
    else -> AppStrings.VERIFICATION_STATUS_NOT_SUBMITTED
}

@Preview(name = "Skill Verification", showBackground = true, showSystemUi = true)
@Composable
private fun SkillVerificationScreenPreview() {
    BerimaTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0)
        ) { padding ->
            SkillVerificationContent(
                user = User(),
                submissions = listOf(
                    VerificationSubmission(
                        status = VerificationStatus.REJECTED,
                        skillCategory = Category.VISUAL,
                        rejectionReason = "Bukti belum cukup jelas."
                    )
                ),
                portfolioItems = listOf(
                    PortfolioItem(
                        portfolioItemId = "portfolio-1",
                        title = "Poster UKM",
                        description = "Desain publikasi acara kampus.",
                        category = Category.VISUAL
                    )
                ),
                selectedCategory = Category.VISUAL,
                selectedPortfolioItemId = null,
                externalLink = "",
                selectedUri = null,
                selectedFileName = null,
                isSubmitting = false,
                onCategorySelected = {},
                onPortfolioSelected = {},
                onExternalLinkChange = {},
                onPickFile = {},
                onSubmit = {},
                modifier = Modifier.padding(padding)
            )
        }
    }
}
