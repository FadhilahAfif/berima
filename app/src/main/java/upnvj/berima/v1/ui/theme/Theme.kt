package upnvj.berima.v1.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val BerimaLightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = BrandOnPrimaryContainer,
    inversePrimary = BrandInversePrimary,
    secondary = BrandSecondary,
    onSecondary = BrandOnSecondary,
    secondaryContainer = BrandSecondaryContainer,
    onSecondaryContainer = BrandOnSecondaryContainer,
    tertiary = BrandTertiary,
    onTertiary = BrandOnTertiary,
    tertiaryContainer = BrandTertiaryContainer,
    onTertiaryContainer = BrandOnTertiaryContainer,
    error = SemanticError,
    onError = SemanticOnError,
    errorContainer = SemanticErrorContainer,
    onErrorContainer = SemanticOnErrorContainer,
    background = BgBackground,
    onBackground = ContentOnBackground,
    surface = BgSurface,
    onSurface = ContentOnSurface,
    onSurfaceVariant = ContentOnSurfaceVariant,
    surfaceVariant = BgSurfaceContainerHighest,
    surfaceTint = BrandPrimary,
    inverseSurface = ContentInverseSurface,
    inverseOnSurface = ContentInverseOnSurface,
    outline = ContentOutline,
    outlineVariant = ContentOutlineVariant,
    surfaceContainerLowest = BgSurfaceContainerLowest,
    surfaceContainerLow = BgSurfaceContainerLow,
    surfaceContainer = BgSurfaceContainer,
    surfaceContainerHigh = BgSurfaceContainerHigh,
    surfaceContainerHighest = BgSurfaceContainerHighest,
    surfaceDim = BgSurfaceDim,
    surfaceBright = BgSurfaceBright,
)

private val BerimaExtendedColors = BerimaColors(
    primaryDim = BrandPrimaryDim,
    surfaceRaised = BgSurfaceRaised,
    surfacePressed = BgSurfacePressed,
    borderSubtle = BorderSubtle,
    borderInput = BorderInput,
    containerGreen = AccentContainerGreen,
    starRating = AccentStarRating,
    textSecondary = ContentTextSecondary,
    statusPending = StatusColors(StatusPendingContainer, StatusPendingText),
    statusInProgress = StatusColors(StatusInProgressContainer, StatusInProgressText),
    statusDelivered = StatusColors(StatusDeliveredContainer, StatusDeliveredText),
    statusCompleted = StatusColors(StatusCompletedContainer, StatusCompletedText),
    statusPaid = StatusColors(StatusPaidContainer, StatusPaidText),
    statusCancelled = StatusColors(StatusCancelledContainer, StatusCancelledText),
    statusRejected = StatusColors(StatusRejectedContainer, StatusRejectedText),
)

@Composable
fun BerimaTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalBerimaColors provides BerimaExtendedColors) {
        MaterialTheme(
            colorScheme = BerimaLightColorScheme,
            typography = BerimaTypography,
            content = content,
        )
    }
}
