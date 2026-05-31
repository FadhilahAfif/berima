package upnvj.berima.v1.ui.profile

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.UserRole
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
import upnvj.berima.v1.ui.theme.BerimaTheme
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val bio by viewModel.bio.collectAsStateWithLifecycle()
    val faculty by viewModel.faculty.collectAsStateWithLifecycle()
    val role by viewModel.role.collectAsStateWithLifecycle()
    val photoUrl by viewModel.photoUrl.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(isSaved) {
        if (isSaved) onNavigateBack()
    }

    val context = LocalContext.current
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.onPhotoPicked(it, context) }
    }

    EditProfileContent(
        name = name,
        bio = bio,
        faculty = faculty,
        role = role,
        photoUrl = photoUrl,
        isLoading = isLoading,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onPickPhoto = { photoPicker.launch("image/*") },
        onNameChange = viewModel::onNameChange,
        onBioChange = viewModel::onBioChange,
        onFacultyChange = viewModel::onFacultyChange,
        onRoleChange = viewModel::onRoleChange,
        onSave = viewModel::save,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileContent(
    name: String,
    bio: String,
    faculty: String,
    role: String,
    photoUrl: String?,
    isLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onPickPhoto: () -> Unit,
    onNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onFacultyChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.EDIT_PROFILE_TITLE,
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            AvatarPicker(
                photoUrl = photoUrl,
                isLoading = isLoading,
                onPickPhoto = onPickPhoto,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            BerimaTextField(
                value = name,
                onValueChange = onNameChange,
                label = AppStrings.EDIT_PROFILE_FIELD_NAME,
                supportingText = { CounterText(name.length, 50) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            BerimaTextField(
                value = bio,
                onValueChange = onBioChange,
                label = AppStrings.EDIT_PROFILE_FIELD_BIO,
                placeholder = AppStrings.EDIT_PROFILE_BIO_PLACEHOLDER,
                singleLine = false,
                maxLines = 4,
                supportingText = { CounterText(bio.length, Validation.MAX_BIO_LENGTH) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            BerimaTextField(
                value = faculty,
                onValueChange = onFacultyChange,
                label = AppStrings.EDIT_PROFILE_FIELD_FACULTY,
                placeholder = AppStrings.EDIT_PROFILE_FACULTY_PLACEHOLDER,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = AppStrings.EDIT_PROFILE_ROLE_LABEL,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = AppStrings.EDIT_PROFILE_ROLE_HELP,
                style = MaterialTheme.typography.bodyMedium,
                color = berimaColors.textSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RoleOption(
                    text = AppStrings.ROLE_BUYER,
                    selected = role == UserRole.BUYER,
                    onClick = { onRoleChange(UserRole.BUYER) },
                    modifier = Modifier.weight(1f)
                )
                RoleOption(
                    text = AppStrings.ROLE_SELLER,
                    selected = role == UserRole.SELLER,
                    onClick = { onRoleChange(UserRole.SELLER) },
                    modifier = Modifier.weight(1f)
                )
                RoleOption(
                    text = AppStrings.ROLE_BOTH,
                    selected = role == UserRole.BOTH,
                    onClick = { onRoleChange(UserRole.BOTH) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            BerimaButton(
                text = AppStrings.EDIT_PROFILE_SAVE,
                onClick = onSave,
                isLoading = isLoading,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AvatarPicker(
    photoUrl: String?,
    isLoading: Boolean,
    onPickPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clickable(onClick = onPickPhoto)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, berimaColors.borderInput, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (photoUrl.isNullOrBlank()) {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(44.dp)
                    )
                } else {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Foto profil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = AppStrings.EDIT_PROFILE_CHANGE_PHOTO,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = AppStrings.EDIT_PROFILE_CHANGE_PHOTO,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .clickable(onClick = onPickPhoto)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun CounterText(current: Int, max: Int) {
    val berimaColors = LocalBerimaColors.current
    Text(
        text = "$current/$max",
        style = MaterialTheme.typography.bodyMedium,
        color = if (current >= max) MaterialTheme.colorScheme.error else berimaColors.textSecondary,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun RoleOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(9999.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            )
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else berimaColors.borderInput,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditProfileContentPreview(modifier: Modifier = Modifier) {
    BerimaTheme {
        EditProfileContent(
            name = "Andi Pratama",
            bio = "Jasa desain PPT dan poster UKM profesional.",
            faculty = "Teknik Informatika",
            role = UserRole.SELLER,
            photoUrl = null,
            isLoading = false,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onPickPhoto = {},
            onNameChange = {},
            onBioChange = {},
            onFacultyChange = {},
            onRoleChange = {},
            onSave = {},
            modifier = modifier
        )
    }
}
