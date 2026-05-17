package upnvj.berima.v1.ui.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
import upnvj.berima.v1.ui.theme.BerimaTheme
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            viewModel.onNavigatedToHome()
            onNavigateToHome()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        modifier = modifier,
    ) { innerPadding ->
        RegisterContent(
            name = uiState.name,
            email = uiState.email,
            password = uiState.password,
            confirmPassword = uiState.confirmPassword,
            isLoading = uiState.isLoading,
            onNameChange = viewModel::onNameChange,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onSubmit = {
                focusManager.clearFocus()
                viewModel.signUp()
            },
            onLoginClick = onNavigateToLogin,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
private fun RegisterContent(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val berimaColors = LocalBerimaColors.current
    val isPreview = LocalInspectionMode.current

    val heroAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val nameAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val nameOffset = remember { Animatable(if (isPreview) 0f else 8f) }
    val emailAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val emailOffset = remember { Animatable(if (isPreview) 0f else 8f) }
    val pwAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val pwOffset = remember { Animatable(if (isPreview) 0f else 8f) }
    val confirmAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val confirmOffset = remember { Animatable(if (isPreview) 0f else 8f) }
    val buttonAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val footerAlpha = remember { Animatable(if (isPreview) 1f else 0f) }

    if (!isPreview) {
        LaunchedEffect(Unit) {
            heroAlpha.animateTo(1f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(200)
            nameAlpha.animateTo(1f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(200)
            nameOffset.animateTo(0f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(260)
            emailAlpha.animateTo(1f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(260)
            emailOffset.animateTo(0f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(320)
            pwAlpha.animateTo(1f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(320)
            pwOffset.animateTo(0f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(380)
            confirmAlpha.animateTo(1f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(380)
            confirmOffset.animateTo(0f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(500)
            buttonAlpha.animateTo(1f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(580)
            footerAlpha.animateTo(1f, animationSpec = tween(400))
        }
    }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Hero block
        Column(modifier = Modifier.alpha(heroAlpha.value)) {
            BrandMarkChip()
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Buat\nakun baru.",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Daftar dan mulai tawarkan jasamu.",
                style = MaterialTheme.typography.bodyLarge,
                color = berimaColors.textSecondary,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Name
        Box(
            modifier = Modifier
                .alpha(nameAlpha.value)
                .graphicsLayer { translationY = nameOffset.value.dp.toPx() },
        ) {
            BerimaTextField(
                value = name,
                onValueChange = onNameChange,
                label = "Nama lengkap",
                placeholder = "Nama sesuai KTM",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = null,
                        tint = berimaColors.textSecondary,
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Email
        Box(
            modifier = Modifier
                .alpha(emailAlpha.value)
                .graphicsLayer { translationY = emailOffset.value.dp.toPx() },
        ) {
            BerimaTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "nama@email.com",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_mail),
                        contentDescription = null,
                        tint = berimaColors.textSecondary,
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Password
        Box(
            modifier = Modifier
                .alpha(pwAlpha.value)
                .graphicsLayer { translationY = pwOffset.value.dp.toPx() },
        ) {
            BerimaTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "••••••••",
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null,
                        tint = berimaColors.textSecondary,
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible) R.drawable.ic_visibility_off
                                else R.drawable.ic_visibility
                            ),
                            contentDescription = if (passwordVisible) "Sembunyikan password"
                            else "Tampilkan password",
                            tint = berimaColors.textSecondary,
                        )
                    }
                },
                supportingText = {
                    Text(
                        text = "Minimal 8 karakter",
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary,
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Confirm password
        Box(
            modifier = Modifier
                .alpha(confirmAlpha.value)
                .graphicsLayer { translationY = confirmOffset.value.dp.toPx() },
        ) {
            BerimaTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Konfirmasi password",
                placeholder = "Ulangi password",
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSubmit() }
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null,
                        tint = berimaColors.textSecondary,
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (confirmPasswordVisible) R.drawable.ic_visibility_off
                                else R.drawable.ic_visibility
                            ),
                            contentDescription = if (confirmPasswordVisible) "Sembunyikan password"
                            else "Tampilkan password",
                            tint = berimaColors.textSecondary,
                        )
                    }
                },
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Box(modifier = Modifier.alpha(buttonAlpha.value)) {
            BerimaButton(
                text = "Daftar",
                onClick = onSubmit,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        FooterLink(
            prefix = "Sudah punya akun?",
            actionLabel = "Masuk",
            onClick = onLoginClick,
            modifier = Modifier.alpha(footerAlpha.value),
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// --- Direction B: Bottom sheet panel variant (preview only) -------------------

// Direction B was rejected on 2026-05-17. Editorial direction is the runtime UI.

// --- Previews -----------------------------------------------------------------

@Preview(name = "Register · Editorial", showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    BerimaTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            RegisterContent(
                name = "",
                email = "",
                password = "",
                confirmPassword = "",
                isLoading = false,
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onSubmit = {},
                onLoginClick = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }
}
