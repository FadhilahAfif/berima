package upnvj.berima.v1.ui.listing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Category
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
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
                        text = "Buat Listing",
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
                text = "Simpan Listing",
                onClick = viewModel::submit,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryDropdown(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val options = listOf(
        Category.ACADEMIC to "Academic Support",
        Category.VISUAL to "Visual Branding",
        Category.DATA to "Data Processing"
    )
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selected }?.second ?: selected

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Kategori", style = MaterialTheme.typography.bodyMedium) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = berimaColors.borderInput,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview(name = "CreateListingScreen", showBackground = true, showSystemUi = true)
@Composable
private fun CreateListingScreenPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        val berimaColors = LocalBerimaColors.current
        androidx.compose.material3.Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text("Buat Listing", style = MaterialTheme.typography.titleMedium) },
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
                BerimaTextField(value = "Desain Logo Profesional", onValueChange = {}, label = "Judul", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                CategoryDropdown(selected = upnvj.berima.v1.data.model.Category.VISUAL, onSelected = {}, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                BerimaTextField(value = "Saya akan membuat desain logo profesional untuk brand kamu.", onValueChange = {}, label = "Deskripsi", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                BerimaTextField(value = "75000", onValueChange = {}, label = "Harga (Rp)", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                BerimaTextField(value = "24", onValueChange = {}, label = "Waktu pengerjaan (jam)", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                BerimaTextField(value = "desain, logo, branding", onValueChange = {}, label = "Tags (opsional)", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(24.dp))
                BerimaButton(text = "Simpan Listing", onClick = {}, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
