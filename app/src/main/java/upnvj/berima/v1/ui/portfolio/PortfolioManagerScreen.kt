package upnvj.berima.v1.ui.portfolio

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.PortfolioItem
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
import upnvj.berima.v1.ui.common.CategoryPickerField
import upnvj.berima.v1.ui.common.CounterText
import upnvj.berima.v1.ui.common.PortfolioCard
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioManagerScreen(
    onNavigateBack: () -> Unit,
    viewModel: PortfolioManagerViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val title by viewModel.title.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val category by viewModel.category.collectAsStateWithLifecycle()
    val externalLink by viewModel.externalLink.collectAsStateWithLifecycle()
    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()
    val selectedImageName by viewModel.selectedImageName.collectAsStateWithLifecycle()
    val editingItem by viewModel.editingItem.collectAsStateWithLifecycle()
    val removeExistingImage by viewModel.removeExistingImage.collectAsStateWithLifecycle()
    val isFormOpen by viewModel.isFormOpen.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val berimaColors = LocalBerimaColors.current
    var deleteCandidate by remember { mutableStateOf<PortfolioItem?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onImageSelected(uri)
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

    deleteCandidate?.let { item ->
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            title = { Text(AppStrings.PORTFOLIO_DELETE_TITLE) },
            text = { Text(AppStrings.PORTFOLIO_DELETE_BODY) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(item)
                        deleteCandidate = null
                    }
                ) {
                    Text(AppStrings.PORTFOLIO_DELETE_CONFIRM)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidate = null }) {
                    Text(AppStrings.PORTFOLIO_DELETE_CANCEL)
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.PORTFOLIO_TITLE,
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
        if (isLoading && items.isEmpty()) {
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
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (!isFormOpen) {
                BerimaButton(
                    text = AppStrings.PORTFOLIO_ADD,
                    onClick = viewModel::openCreateForm,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(18.dp))
            } else {
                PortfolioForm(
                    title = title,
                    description = description,
                    category = category,
                    externalLink = externalLink,
                    selectedImageUri = selectedImageUri,
                    selectedImageName = selectedImageName,
                    existingImageUrl = editingItem?.imageUrl.takeUnless { removeExistingImage },
                    isEditing = editingItem != null,
                    isSaving = isSaving,
                    onTitleChange = viewModel::onTitleChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onCategoryChange = viewModel::onCategoryChange,
                    onExternalLinkChange = viewModel::onExternalLinkChange,
                    onPickImage = { imagePicker.launch("image/*") },
                    onRemoveImage = viewModel::removeCurrentImage,
                    onCancel = viewModel::closeForm,
                    onSave = viewModel::save
                )
                Spacer(Modifier.height(24.dp))
            }

            Text(
                text = AppStrings.PORTFOLIO_LIST_TITLE,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))

            if (items.isEmpty()) {
                EmptyPortfolioManagerState()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items.forEach { item ->
                        PortfolioCard(
                            item = item,
                            trailingContent = {
                                PortfolioItemActions(
                                    onEdit = { viewModel.openEditForm(item) },
                                    onDelete = { deleteCandidate = item }
                                )
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PortfolioForm(
    title: String,
    description: String,
    category: String,
    externalLink: String,
    selectedImageUri: Uri?,
    selectedImageName: String?,
    existingImageUrl: String?,
    isEditing: Boolean,
    isSaving: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onExternalLinkChange: (String) -> Unit,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = if (isEditing) AppStrings.PORTFOLIO_FORM_EDIT_TITLE else AppStrings.PORTFOLIO_FORM_CREATE_TITLE,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(14.dp))
        BerimaTextField(
            value = title,
            onValueChange = onTitleChange,
            label = AppStrings.PORTFOLIO_FIELD_TITLE,
            placeholder = AppStrings.PORTFOLIO_TITLE_PLACEHOLDER,
            supportingText = {
                CounterText(
                    current = title.length,
                    max = Validation.MAX_PORTFOLIO_TITLE_LENGTH
                )
            }
        )
        Spacer(Modifier.height(12.dp))
        CategoryPickerField(
            selected = category,
            onSelected = onCategoryChange,
            label = AppStrings.PORTFOLIO_FIELD_CATEGORY
        )
        Spacer(Modifier.height(12.dp))
        BerimaTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = AppStrings.PORTFOLIO_FIELD_DESCRIPTION,
            placeholder = AppStrings.PORTFOLIO_DESCRIPTION_PLACEHOLDER,
            singleLine = false,
            maxLines = 5,
            supportingText = {
                CounterText(
                    current = description.length,
                    max = Validation.MAX_PORTFOLIO_DESCRIPTION_LENGTH
                )
            }
        )
        Spacer(Modifier.height(12.dp))
        BerimaTextField(
            value = externalLink,
            onValueChange = onExternalLinkChange,
            label = AppStrings.PORTFOLIO_FIELD_LINK,
            placeholder = AppStrings.PORTFOLIO_LINK_PLACEHOLDER
        )
        Spacer(Modifier.height(12.dp))
        PortfolioImagePicker(
            selectedImageUri = selectedImageUri,
            selectedImageName = selectedImageName,
            existingImageUrl = existingImageUrl,
            onPickImage = onPickImage,
            onRemoveImage = onRemoveImage
        )
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(AppStrings.PORTFOLIO_CANCEL)
            }
            BerimaButton(
                text = AppStrings.PORTFOLIO_SAVE,
                onClick = onSave,
                isLoading = isSaving,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PortfolioImagePicker(
    selectedImageUri: Uri?,
    selectedImageName: String?,
    existingImageUrl: String?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(berimaColors.surfaceRaised)
            .border(1.dp, berimaColors.borderSubtle, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = AppStrings.PORTFOLIO_IMAGE_TITLE,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                val imageModel: Any? = selectedImageUri ?: existingImageUrl
                if (imageModel == null) {
                    Icon(
                        painter = painterResource(R.drawable.ic_category_visual),
                        contentDescription = null,
                        tint = berimaColors.textSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = AppStrings.PORTFOLIO_IMAGE_TITLE,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = selectedImageName
                        ?: if (existingImageUrl != null) AppStrings.PORTFOLIO_IMAGE_ATTACHED else AppStrings.PORTFOLIO_IMAGE_EMPTY,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = AppStrings.PORTFOLIO_IMAGE_HELP,
                    style = MaterialTheme.typography.bodyMedium,
                    color = berimaColors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = if (selectedImageUri == null && existingImageUrl == null) {
                    AppStrings.PORTFOLIO_PICK_IMAGE
                } else {
                    AppStrings.PORTFOLIO_CHANGE_IMAGE
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .clickable(onClick = onPickImage)
                    .padding(vertical = 6.dp)
            )
            if (selectedImageUri != null || existingImageUrl != null) {
                Text(
                    text = AppStrings.PORTFOLIO_REMOVE_IMAGE,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .clickable(onClick = onRemoveImage)
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun PortfolioItemActions(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = AppStrings.PORTFOLIO_EDIT,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = AppStrings.PORTFOLIO_DELETE,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .clickable(onClick = onDelete)
                .padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun EmptyPortfolioManagerState(modifier: Modifier = Modifier) {
    val berimaColors = LocalBerimaColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, RoundedCornerShape(16.dp))
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = AppStrings.PORTFOLIO_EMPTY_MANAGE,
            style = MaterialTheme.typography.bodyMedium,
            color = berimaColors.textSecondary
        )
    }
}
