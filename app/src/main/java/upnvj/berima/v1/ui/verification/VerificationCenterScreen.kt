package upnvj.berima.v1.ui.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Category
import upnvj.berima.v1.data.model.User
import upnvj.berima.v1.data.model.VerificationStatus
import upnvj.berima.v1.data.model.VerificationSubmission
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.categoryFullLabel
import upnvj.berima.v1.ui.common.categoryIconRes
import upnvj.berima.v1.ui.theme.BerimaTheme
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import upnvj.berima.v1.ui.theme.StatusColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationCenterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIdentity: () -> Unit,
    onNavigateToSkill: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VerificationCenterViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val identitySubmission by viewModel.identitySubmission.collectAsStateWithLifecycle()
    val skillSubmissions by viewModel.skillSubmissions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val berimaColors = LocalBerimaColors.current

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
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
                        text = AppStrings.VERIFICATION_TITLE,
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
        when {
            isLoading && user == null -> {
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
            }

            user == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = AppStrings.PROFILE_NOT_FOUND,
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                VerificationCenterContent(
                    user = user!!,
                    identitySubmission = identitySubmission,
                    skillSubmissions = skillSubmissions,
                    onIdentityClick = onNavigateToIdentity,
                    onSkillClick = onNavigateToSkill,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }
}

@Composable
private fun VerificationCenterContent(
    user: User,
    identitySubmission: VerificationSubmission?,
    skillSubmissions: List<VerificationSubmission>,
    onIdentityClick: () -> Unit,
    onSkillClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val identityStatus = identitySubmission?.status ?: user.identityVerificationStatus
    val skillSubmission = skillSubmissions.firstOrNull()
    val skillStatus = skillStatus(user.verifiedSkillBadges, skillSubmission)
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = AppStrings.VERIFICATION_SUBTITLE,
            style = MaterialTheme.typography.bodyLarge,
            color = berimaColors.textSecondary
        )
        Spacer(Modifier.height(20.dp))
        VerificationSectionCard(
            title = AppStrings.VERIFICATION_IDENTITY_TITLE,
            body = AppStrings.VERIFICATION_IDENTITY_BODY,
            status = identityStatus,
            iconRes = R.drawable.ic_person,
            rejectionReason = identitySubmission?.rejectionReason,
            onClick = onIdentityClick
        )
        Spacer(Modifier.height(12.dp))
        VerificationSectionCard(
            title = AppStrings.VERIFICATION_SKILL_TITLE,
            body = AppStrings.VERIFICATION_SKILL_BODY,
            status = skillStatus,
            iconRes = R.drawable.ic_check,
            skillBadges = user.verifiedSkillBadges,
            rejectionReason = skillSubmission?.rejectionReason,
            onClick = onSkillClick
        )
    }
}

@Composable
private fun VerificationSectionCard(
    title: String,
    body: String,
    status: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
    skillBadges: List<String> = emptyList(),
    rejectionReason: String? = null,
    onClick: () -> Unit = {}
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, shape)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(berimaColors.containerGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(21.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = berimaColors.textSecondary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AppStrings.VERIFICATION_STATUS_LABEL,
                style = MaterialTheme.typography.labelSmall,
                color = berimaColors.textSecondary
            )
            Spacer(Modifier.width(10.dp))
            VerificationStatusPill(status = status)
        }

        if (status == VerificationStatus.REJECTED) {
            Spacer(Modifier.height(14.dp))
            RejectionCallout(reason = rejectionReason)
        }

        if (skillBadges.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skillBadges.forEach { category ->
                    SkillBadge(category = category)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = verificationActionLabel(status),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun RejectionCallout(
    reason: String?,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
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
            color = berimaColors.textSecondary
        )
    }
}

@Composable
private fun VerificationStatusPill(
    status: String,
    modifier: Modifier = Modifier
) {
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
private fun SkillBadge(
    category: String,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(berimaColors.containerGreen)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(categoryIconRes(category)),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = categoryFullLabel(category),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
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

private fun skillStatus(
    skillBadges: List<String>,
    latestSubmission: VerificationSubmission?
): String {
    return when {
        skillBadges.isNotEmpty() -> VerificationStatus.APPROVED
        latestSubmission != null -> latestSubmission.status
        else -> VerificationStatus.NOT_SUBMITTED
    }
}

private fun verificationActionLabel(status: String): String = when (status) {
    VerificationStatus.REJECTED -> AppStrings.VERIFICATION_ACTION_RESUBMIT
    VerificationStatus.PENDING,
    VerificationStatus.APPROVED -> AppStrings.VERIFICATION_ACTION_VIEW_STATUS
    else -> AppStrings.VERIFICATION_ACTION_SUBMIT
}

@Preview(name = "Verification Center", showBackground = true, showSystemUi = true)
@Composable
private fun VerificationCenterScreenPreview() {
    BerimaTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0)
        ) { padding ->
            VerificationCenterContent(
                user = User(
                    name = "Rina Amelia",
                    email = "rina@email.com",
                    identityVerificationStatus = VerificationStatus.PENDING,
                    verifiedSkillBadges = listOf(Category.VISUAL, Category.DATA)
                ),
                identitySubmission = VerificationSubmission(status = VerificationStatus.PENDING),
                skillSubmissions = listOf(VerificationSubmission(status = VerificationStatus.APPROVED)),
                onIdentityClick = {},
                onSkillClick = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}
