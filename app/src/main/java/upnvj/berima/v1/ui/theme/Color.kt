package upnvj.berima.v1.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val BgBackground = Color(0xFFF2EFE9)
val BgSurface = Color(0xFFFFFFFF)
val BgSurfaceDim = Color(0xFFDCD9D9)
val BgSurfaceBright = Color(0xFFFCF9F8)
val BgSurfaceContainerLowest = Color(0xFFFFFFFF)
val BgSurfaceContainerLow = Color(0xFFF6F3F2)
val BgSurfaceContainer = Color(0xFFF0EDED)
val BgSurfaceContainerHigh = Color(0xFFEAE7E7)
val BgSurfaceContainerHighest = Color(0xFFE5E2E1)
val BgSurfaceRaised = Color(0xFFF7F5F0)
val BgSurfacePressed = Color(0xFFEEEBE4)

val ContentOnSurface = Color(0xFF1A1A1A)
val ContentOnSurfaceVariant = Color(0xFF404943)
val ContentOnBackground = Color(0xFF1A1A1A)
val ContentInverseSurface = Color(0xFF313030)
val ContentInverseOnSurface = Color(0xFFF3F0EF)
val ContentOutline = Color(0xFF707973)
val ContentOutlineVariant = Color(0xFFBFC9C1)
val ContentTextSecondary = Color(0xFF6B6864)

val BorderSubtle = Color(0xFFE8E5E0)
val BorderInput = Color(0xFFD0CCC8)

val BrandPrimary = Color(0xFF2D6A4F)
val BrandPrimaryDim = Color(0xFF3D8B68)
val BrandOnPrimary = Color(0xFFFFFFFF)
val BrandPrimaryContainer = Color(0xFF0F5238)
val BrandOnPrimaryContainer = Color(0xFFA8E7C5)
val BrandInversePrimary = Color(0xFF95D4B3)
val BrandPrimaryFixed = Color(0xFFB1F0CE)
val BrandPrimaryFixedDim = Color(0xFF95D4B3)
val BrandOnPrimaryFixed = Color(0xFF002114)
val BrandOnPrimaryFixedVariant = Color(0xFF0E5138)

val BrandSecondary = Color(0xFF5F5E5A)
val BrandOnSecondary = Color(0xFFFFFFFF)
val BrandSecondaryContainer = Color(0xFFE5E2DC)
val BrandOnSecondaryContainer = Color(0xFF656460)
val BrandSecondaryFixed = Color(0xFFE5E2DC)
val BrandSecondaryFixedDim = Color(0xFFC9C6C1)
val BrandOnSecondaryFixed = Color(0xFF1C1C18)
val BrandOnSecondaryFixedVariant = Color(0xFF474743)

val BrandTertiary = Color(0xFF005236)
val BrandOnTertiary = Color(0xFFFFFFFF)
val BrandTertiaryContainer = Color(0xFF006D48)
val BrandOnTertiaryContainer = Color(0xFF89EDBA)
val BrandTertiaryFixed = Color(0xFF92F7C3)
val BrandTertiaryFixedDim = Color(0xFF75DAA8)
val BrandOnTertiaryFixed = Color(0xFF002113)
val BrandOnTertiaryFixedVariant = Color(0xFF005235)

val SemanticError = Color(0xFFBA1A1A)
val SemanticOnError = Color(0xFFFFFFFF)
val SemanticErrorContainer = Color(0xFFFFDAD6)
val SemanticOnErrorContainer = Color(0xFF93000A)

val AccentContainerGreen = Color(0xFFD4EDE3)
val AccentStarRating = Color(0xFFFBBF24)

// Category thumbnail placeholders. Green-only palette constraint: categories are
// differentiated by lightness + chroma and the category glyph, never by hue family.
val CategoryAcademicContainer = Color(0xFFDCE9E1)
val CategoryAcademicGlyph = Color(0xFF1F5740)
val CategoryVisualContainer = Color(0xFFCDE9DB)
val CategoryVisualGlyph = Color(0xFF0F5238)
val CategoryDataContainer = Color(0xFFE4EAE4)
val CategoryDataGlyph = Color(0xFF3C5A4B)

val StatusPendingContainer = Color(0xFFFFF4D6)
val StatusPendingText = Color(0xFF7A5800)
val StatusInProgressContainer = Color(0xFFE5EFE9)
val StatusInProgressText = Color(0xFF0F5238)
val StatusDeliveredContainer = Color(0xFFD4EDE3)
val StatusDeliveredText = Color(0xFF005236)
val StatusCompletedContainer = Color(0xFFB1F0CE)
val StatusCompletedText = Color(0xFF002114)
val StatusPaidContainer = Color(0xFF2D6A4F)
val StatusPaidText = Color(0xFFFFFFFF)
val StatusCancelledContainer = Color(0xFFE5E2E1)
val StatusCancelledText = Color(0xFF404943)
val StatusRejectedContainer = Color(0xFFFFDAD6)
val StatusRejectedText = Color(0xFF93000A)

@Immutable
data class StatusColors(val container: Color, val text: Color)

@Immutable
data class CategoryColors(val container: Color, val glyph: Color)

@Immutable
data class BerimaColors(
    val primaryDim: Color,
    val surfaceRaised: Color,
    val surfacePressed: Color,
    val borderSubtle: Color,
    val borderInput: Color,
    val containerGreen: Color,
    val starRating: Color,
    val textSecondary: Color,
    val categoryAcademic: CategoryColors,
    val categoryVisual: CategoryColors,
    val categoryData: CategoryColors,
    val statusPending: StatusColors,
    val statusInProgress: StatusColors,
    val statusDelivered: StatusColors,
    val statusCompleted: StatusColors,
    val statusPaid: StatusColors,
    val statusCancelled: StatusColors,
    val statusRejected: StatusColors,
)

val LocalBerimaColors = staticCompositionLocalOf {
    BerimaColors(
        primaryDim = BrandPrimaryDim,
        surfaceRaised = BgSurfaceRaised,
        surfacePressed = BgSurfacePressed,
        borderSubtle = BorderSubtle,
        borderInput = BorderInput,
        containerGreen = AccentContainerGreen,
        starRating = AccentStarRating,
        textSecondary = ContentTextSecondary,
        categoryAcademic = CategoryColors(CategoryAcademicContainer, CategoryAcademicGlyph),
        categoryVisual = CategoryColors(CategoryVisualContainer, CategoryVisualGlyph),
        categoryData = CategoryColors(CategoryDataContainer, CategoryDataGlyph),
        statusPending = StatusColors(StatusPendingContainer, StatusPendingText),
        statusInProgress = StatusColors(StatusInProgressContainer, StatusInProgressText),
        statusDelivered = StatusColors(StatusDeliveredContainer, StatusDeliveredText),
        statusCompleted = StatusColors(StatusCompletedContainer, StatusCompletedText),
        statusPaid = StatusColors(StatusPaidContainer, StatusPaidText),
        statusCancelled = StatusColors(StatusCancelledContainer, StatusCancelledText),
        statusRejected = StatusColors(StatusRejectedContainer, StatusRejectedText),
    )
}
