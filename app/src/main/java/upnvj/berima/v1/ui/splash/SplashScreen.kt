package upnvj.berima.v1.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import upnvj.berima.v1.ui.common.AppStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.ui.theme.BerimaTheme
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Home -> onNavigateToHome()
            SplashDestination.Login -> onNavigateToLogin()
            SplashDestination.Idle -> Unit
        }
    }

    SplashContent(modifier = modifier)
}

@Composable
private fun SplashContent(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    val markScale = remember { Animatable(if (isPreview) 1f else 0.7f) }
    val markAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val taglineAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    var showLoader by remember { mutableStateOf(isPreview) }

    if (!isPreview) {
        LaunchedEffect(Unit) {
            markScale.animateTo(1f, animationSpec = tween(500, easing = EaseOutCubic))
        }
        LaunchedEffect(Unit) {
            markAlpha.animateTo(1f, animationSpec = tween(500))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(400)
            taglineAlpha.animateTo(1f, animationSpec = tween(600))
        }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(600)
            showLoader = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues()),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_berima_logo),
                contentDescription = "Berima",
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .scale(markScale.value)
                    .alpha(markAlpha.value),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = AppStrings.SPLASH_TAGLINE,
                style = MaterialTheme.typography.bodyMedium,
                color = LocalBerimaColors.current.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha.value),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (showLoader) {
                PulsingDots()
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }
            Text(
                text = "BERIMA · 2026",
                style = MaterialTheme.typography.labelSmall,
                color = LocalBerimaColors.current.textSecondary,
            )
        }
    }
}

@Composable
private fun PulsingDots(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "splashDots")
    val color = LocalBerimaColors.current.borderInput
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val phase = index * 200
            val alpha by transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, delayMillis = phase, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "dotAlpha$index",
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    BerimaTheme {
        SplashContent()
    }
}
