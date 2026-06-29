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
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.data.model.VerificationStatus
import upnvj.berima.v1.data.model.VerificationSubmission
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
import upnvj.berima.v1.ui.theme.BerimaTheme
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import upnvj.berima.v1.ui.theme.StatusColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentityVerificationScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IdentityVerificationViewModel = hiltViewModel()
) {
    val submission by viewModel.submission.collectAsStateWithLifecycle()
    val selectedUri by viewModel.selectedUri.collectAsStateWithLifecycle()
    val selectedFileName by viewModel.selectedFileName.collectAsStateWithLifecycle()
    val note by viewModel.note.collectAsStateWithLifecycle()
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
                        text = AppStrings.IDENTITY_TITLE,
                        style = MaterialTheme.typography.headlineMedium,
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
        if (isLoading && submission == null) {
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
            IdentityVerificationContent(
                submission = submission,
                selectedUri = selectedUri,
                selectedFileName = selectedFileName,
                note = note,
                isSubmitting = isSubmitting,
                onPickFile = { filePicker.launch("*/*") },
                onNoteChange = viewModel::onNoteChange,
                onSubmit = viewModel::submit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}

@Composable
private fun IdentityVerificationContent(
    submission: VerificationSubmission?,
    selectedUri: Uri?,
    selectedFileName: String?,
    note: String,
    isSubmitting: Boolean,
    onPickFile: () -> Unit,
    onNoteChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = submission?.status ?: VerificationStatus.NOT_SUBMITTED
    val canSubmit = status == VerificationStatus.NOT_SUBMITTED ||
        status == VerificationStatus.REJECTED

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        VerificationStatusCard(
            title = AppStrings.IDENTITY_TITLE,
            body = AppStrings.IDENTITY_STATUS_BODY,
            status = status,
            rejectionReason = submission?.rejectionReason
        )
        Spacer(Modifier.height(16.dp))

        if (canSubmit) {
            FilePickerCard(
                title = AppStrings.IDENTITY_UPLOAD_TITLE,
                body = AppStrings.IDENTITY_UPLOAD_BODY,
                selectedFileName = selectedFileName ?: selectedUri?.lastPathSegment,
                pickLabel = if (selectedUri == null) {
                    AppStrings.IDENTITY_PICK_FILE
                } else {
                    AppStrings.IDENTITY_CHANGE_FILE
                },
                onPickFile = onPickFile
            )
            Spacer(Modifier.height(16.dp))
            BerimaTextField(
                value = note,
                onValueChange = onNoteChange,
                label = AppStrings.IDENTITY_NOTE_LABEL,
                placeholder = AppStrings.IDENTITY_NOTE_PLACEHOLDER,
                singleLine = false,
                maxLines = 4,
                supportingText = {
                    Text(
                        text = "${note.length}/${Validation.MAX_VERIFICATION_NOTE_LENGTH}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
            Spacer(Modifier.height(20.dp))
            BerimaButton(
                text = AppStrings.IDENTITY_SUBMIT,
                onClick = onSubmit,
                isLoading = isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun VerificationStatusCard(
    title: String,
    body: String,
    status: String,
    modifier: Modifier = Modifier,
    rejectionReason: String? = null
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
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                VerificationStatusPill(status = status)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = body,
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
private fun FilePickerCard(
    title: String,
    body: String,
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
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = body,
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

@Preview(name = "Identity Verification", showBackground = true, showSystemUi = true)
@Composable
private fun IdentityVerificationScreenPreview() {
    BerimaTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0)
        ) { padding ->
            IdentityVerificationContent(
                submission = VerificationSubmission(status = VerificationStatus.REJECTED),
                selectedUri = null,
                selectedFileName = null,
                note = "",
                isSubmitting = false,
                onPickFile = {},
                onNoteChange = {},
                onSubmit = {},
                modifier = Modifier.padding(padding)
            )
        }
    }
}
