package upnvj.berima.v1.ui.listing

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Category
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateListingViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val category by viewModel.category.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val price by viewModel.price.collectAsStateWithLifecycle()
    val deliveryTimeHours by viewModel.deliveryTimeHours.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val selectedThumbnailUri by viewModel.selectedThumbnailUri.collectAsStateWithLifecycle()
    val isPolicyAccepted by viewModel.isPolicyAccepted.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val berimaColors = LocalBerimaColors.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onThumbnailSelected(uri)
    }

    LaunchedEffect(success) {
        if (success) onNavigateBack()
    }

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
                        text = AppStrings.LISTING_CREATE_TITLE,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ListingFormContent(
                title = title,
                onTitleChange = viewModel::onTitleChange,
                category = category,
                onCategoryChange = viewModel::onCategoryChange,
                description = description,
                onDescriptionChange = viewModel::onDescriptionChange,
                price = price,
                onPriceChange = viewModel::onPriceChange,
                deliveryTimeHours = deliveryTimeHours,
                onDeliveryTimeChange = viewModel::onDeliveryTimeChange,
                tags = tags,
                onTagsChange = viewModel::onTagsChange,
                selectedThumbnailUri = selectedThumbnailUri,
                existingThumbnailUrl = null,
                isRemovingExistingThumbnail = false,
                onPickThumbnail = { imagePicker.launch("image/*") },
                onRemoveThumbnail = viewModel::removeThumbnail,
                isPolicyAccepted = isPolicyAccepted,
                onPolicyAcceptedChange = viewModel::onPolicyAcceptedChange,
                isLoading = isLoading,
                submitLabel = AppStrings.LISTING_SAVE_CREATE,
                onSubmit = viewModel::submit
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview(name = "CreateListingScreen", showBackground = true, showSystemUi = true)
@Composable
private fun CreateListingScreenPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        val berimaColors = LocalBerimaColors.current
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            AppStrings.LISTING_CREATE_TITLE,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = berimaColors.surfaceRaised)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                ListingFormContent(
                    title = "Desain Logo Profesional",
                    onTitleChange = {},
                    category = Category.VISUAL,
                    onCategoryChange = {},
                    description = "Saya akan membuat desain logo profesional untuk brand kamu.",
                    onDescriptionChange = {},
                    price = "75000",
                    onPriceChange = {},
                    deliveryTimeHours = "24",
                    onDeliveryTimeChange = {},
                    tags = "desain, logo, branding",
                    onTagsChange = {},
                    selectedThumbnailUri = null,
                    existingThumbnailUrl = null,
                    isRemovingExistingThumbnail = false,
                    onPickThumbnail = {},
                    onRemoveThumbnail = {},
                    isPolicyAccepted = true,
                    onPolicyAcceptedChange = {},
                    isLoading = false,
                    submitLabel = AppStrings.LISTING_SAVE_CREATE,
                    onSubmit = {}
                )
            }
        }
    }
}
