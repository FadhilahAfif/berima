package upnvj.berima.v1.ui.listing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditListingScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditListingViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val category by viewModel.category.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val price by viewModel.price.collectAsStateWithLifecycle()
    val deliveryTimeHours by viewModel.deliveryTimeHours.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val berimaColors = LocalBerimaColors.current

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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Listing",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Kembali",
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
        if (isLoading && title.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
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
            BerimaTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = "Judul",
                placeholder = "Maks. 60 karakter",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            CategoryDropdown(
                selected = category,
                onSelected = viewModel::onCategoryChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            BerimaTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                label = "Deskripsi",
                placeholder = "Maks. 500 karakter",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            BerimaTextField(
                value = price,
                onValueChange = viewModel::onPriceChange,
                label = "Harga (Rp)",
                placeholder = "Contoh: 50000",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            BerimaTextField(
                value = deliveryTimeHours,
                onValueChange = viewModel::onDeliveryTimeChange,
                label = "Waktu pengerjaan (jam)",
                placeholder = "Maks. 48 jam",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            BerimaTextField(
                value = tags,
                onValueChange = viewModel::onTagsChange,
                label = "Tags (opsional)",
                placeholder = "Pisahkan dengan koma, contoh: desain, logo",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            BerimaButton(
                text = "Simpan Perubahan",
                onClick = viewModel::submit,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "EditListingScreen", showBackground = true, showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditListingScreenPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        val berimaColors = upnvj.berima.v1.ui.theme.LocalBerimaColors.current
        androidx.compose.material3.Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text("Edit Listing", style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        androidx.compose.material3.IconButton(onClick = {}) {
                            Icon(painter = androidx.compose.ui.res.painterResource(upnvj.berima.v1.R.drawable.ic_arrow_back), contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = berimaColors.surfaceRaised)
                )
            }
        ) { padding ->
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                upnvj.berima.v1.ui.common.BerimaTextField(value = "Desain Logo Profesional", onValueChange = {}, label = "Judul", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                CategoryDropdown(selected = upnvj.berima.v1.data.model.Category.VISUAL, onSelected = {}, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                upnvj.berima.v1.ui.common.BerimaTextField(value = "Saya akan membuat desain logo profesional.", onValueChange = {}, label = "Deskripsi", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                upnvj.berima.v1.ui.common.BerimaTextField(value = "75000", onValueChange = {}, label = "Harga (Rp)", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                upnvj.berima.v1.ui.common.BerimaTextField(value = "24", onValueChange = {}, label = "Waktu pengerjaan (jam)", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                upnvj.berima.v1.ui.common.BerimaTextField(value = "desain, logo", onValueChange = {}, label = "Tags (opsional)", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(24.dp))
                upnvj.berima.v1.ui.common.BerimaButton(text = "Simpan Perubahan", onClick = {}, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
