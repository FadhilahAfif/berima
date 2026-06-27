package upnvj.berima.v1.ui.auth

import android.content.Context
import upnvj.berima.v1.ui.common.AppStrings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import upnvj.berima.v1.R
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
import upnvj.berima.v1.ui.theme.BerimaTheme
import upnvj.berima.v1.ui.theme.LocalBerimaColors

private const val MISSING_WEB_CLIENT_ID = "MISSING_WEB_CLIENT_ID"

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val serverClientId = stringResource(R.string.berima_web_client_id)

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

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        modifier = modifier,
    ) { innerPadding ->
        LoginContent(
            email = uiState.email,
            password = uiState.password,
            isLoading = uiState.isLoading,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onSubmit = {
                focusManager.clearFocus()
                viewModel.signIn()
            },
            onForgotPasswordClick = {
                focusManager.clearFocus()
                viewModel.sendPasswordResetEmail()
            },
            onGoogleSignInClick = {
                focusManager.clearFocus()
                coroutineScope.launch {
                    if (serverClientId == MISSING_WEB_CLIENT_ID || serverClientId.isBlank()) {
                        snackbarHostState.showSnackbar(AppStrings.LOGIN_GOOGLE_UNAVAILABLE)
                        return@launch
                    }
                    launchGoogleSignIn(
                        context = context,
                        serverClientId = serverClientId,
                        onIdToken = viewModel::signInWithGoogle,
                        onCancelled = viewModel::onGoogleSignInCancelled,
                        onError = { message ->
                            snackbarHostState.showSnackbar(message)
                        }
                    )
                }
            },
            onRegisterClick = onNavigateToRegister,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
private fun LoginContent(
    email: String,
    password: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val berimaColors = LocalBerimaColors.current
    val isPreview = LocalInspectionMode.current

    val heroAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val field1Alpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val field1Offset = remember { Animatable(if (isPreview) 0f else 8f) }
    val field2Alpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val field2Offset = remember { Animatable(if (isPreview) 0f else 8f) }
    val buttonAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val footerAlpha = remember { Animatable(if (isPreview) 1f else 0f) }

    if (!isPreview) {
        LaunchedEffect(Unit) {
            heroAlpha.animateTo(1f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(200)
            field1Alpha.animateTo(1f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(200)
            field1Offset.animateTo(0f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(260)
            field2Alpha.animateTo(1f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(260)
            field2Offset.animateTo(0f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(380)
            buttonAlpha.animateTo(1f, animationSpec = tween(400))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(460)
            footerAlpha.animateTo(1f, animationSpec = tween(400))
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        // Hero block
        Column(modifier = Modifier.alpha(heroAlpha.value)) {
            BrandMarkChip()
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = AppStrings.LOGIN_HEADLINE,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = AppStrings.LOGIN_SUBHEADLINE,
                style = MaterialTheme.typography.bodyLarge,
                color = berimaColors.textSecondary,
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Email field
        var passwordVisible by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .alpha(field1Alpha.value)
                .graphicsLayer { translationY = field1Offset.value.dp.toPx() },
        ) {
            BerimaTextField(
                value = email,
                onValueChange = onEmailChange,
                label = AppStrings.LOGIN_EMAIL_LABEL,
                placeholder = AppStrings.LOGIN_EMAIL_PLACEHOLDER,
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

        // Password field
        Box(
            modifier = Modifier
                .alpha(field2Alpha.value)
                .graphicsLayer { translationY = field2Offset.value.dp.toPx() },
        ) {
            BerimaTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = AppStrings.LOGIN_PASSWORD_LABEL,
                placeholder = AppStrings.LOGIN_PASSWORD_PLACEHOLDER,
                visualTransformation = if (passwordVisible) VisualTransformation.None
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
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible) R.drawable.ic_visibility_off
                                else R.drawable.ic_visibility
                            ),
                            contentDescription = if (passwordVisible) AppStrings.LOGIN_PASSWORD_HIDE
                            else AppStrings.LOGIN_PASSWORD_SHOW,
                            tint = berimaColors.textSecondary,
                        )
                    }
                },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = AppStrings.LOGIN_FORGOT_PASSWORD,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onForgotPasswordClick)
                .padding(vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(modifier = Modifier.alpha(buttonAlpha.value)) {
            BerimaButton(
                text = AppStrings.LOGIN_SUBMIT,
                onClick = onSubmit,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        OrDivider(modifier = Modifier.alpha(buttonAlpha.value))

        Spacer(modifier = Modifier.height(18.dp))

        GoogleSignInButton(
            onClick = onGoogleSignInClick,
            enabled = !isLoading,
            modifier = Modifier.alpha(buttonAlpha.value)
        )

        Spacer(modifier = Modifier.height(20.dp))

        FooterLink(
            prefix = AppStrings.LOGIN_REGISTER_PREFIX,
            actionLabel = AppStrings.LOGIN_REGISTER_ACTION,
            onClick = onRegisterClick,
            modifier = Modifier.alpha(footerAlpha.value),
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

private suspend fun launchGoogleSignIn(
    context: Context,
    serverClientId: String,
    onIdToken: (String) -> Unit,
    onCancelled: () -> Unit,
    onError: suspend (String) -> Unit,
) {
    val credentialManager = CredentialManager.create(context)
    val googleOption = GetSignInWithGoogleOption.Builder(serverClientId).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleOption)
        .build()

    try {
        val result = credentialManager.getCredential(
            context = context,
            request = request
        )
        val credential = result.credential
        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            onIdToken(googleCredential.idToken)
        } else {
            onError(AppStrings.LOGIN_GOOGLE_INVALID)
        }
    } catch (e: GetCredentialCancellationException) {
        onCancelled()
        onError(AppStrings.LOGIN_GOOGLE_CANCELLED)
    } catch (e: GetCredentialException) {
        onError(AppStrings.LOGIN_GOOGLE_UNAVAILABLE)
    } catch (e: Exception) {
        onError(AppStrings.LOGIN_GOOGLE_INVALID)
    }
}

@Composable
private fun OrDivider(modifier: Modifier = Modifier) {
    val berimaColors = LocalBerimaColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            color = berimaColors.borderSubtle,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = AppStrings.LOGIN_OR,
            style = MaterialTheme.typography.labelSmall,
            color = berimaColors.textSecondary,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(
            color = berimaColors.borderSubtle,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GoogleSignInButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(9999.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderInput, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = AppStrings.LOGIN_GOOGLE_MARK,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = AppStrings.LOGIN_WITH_GOOGLE,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun BrandMarkChip(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_berima_logo),
        contentDescription = "Berima",
        modifier = modifier
            .width(140.dp)
            .wrapContentHeight(),
    )
}

@Composable
internal fun FooterLink(
    prefix: String,
    actionLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val berimaColors = LocalBerimaColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = berimaColors.textSecondary)) {
                    append("$prefix  ")
                }
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                    )
                ) {
                    append("$actionLabel  →")
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

// --- Direction B: Bottom sheet panel variant (preview only) -------------------

// Direction B was rejected on 2026-05-17. Editorial direction is the runtime UI.

// --- Previews -----------------------------------------------------------------

@Preview(name = "Login · Editorial", showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    BerimaTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            LoginContent(
                email = "",
                password = "",
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onSubmit = {},
                onForgotPasswordClick = {},
                onGoogleSignInClick = {},
                onRegisterClick = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }
}
