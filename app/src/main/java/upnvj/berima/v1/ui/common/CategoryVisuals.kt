package upnvj.berima.v1.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Category
import upnvj.berima.v1.ui.theme.CategoryColors
import upnvj.berima.v1.ui.theme.LocalBerimaColors

/**
 * Single source of truth for how each listing [Category] is presented in the UI:
 * its tinted placeholder colors, its glyph, and its short Bahasa Indonesia label.
 *
 * Used by [ListingCard] (thumbnail placeholder) and HomeScreen's category rail so
 * the visual language stays consistent across every surface that shows a category.
 */
data class CategoryVisual(
    val id: String?,
    val label: String,
    @DrawableRes val iconRes: Int?
)

/** Ordered list for the Home category rail. `null` id = "Semua" (all), shown text-only. */
val categoryVisuals: List<CategoryVisual> = listOf(
    CategoryVisual(null, "Semua", null),
    CategoryVisual(Category.ACADEMIC, "Akademik", R.drawable.ic_category_academic),
    CategoryVisual(Category.VISUAL, "Desain", R.drawable.ic_category_visual),
    CategoryVisual(Category.DATA, "Data", R.drawable.ic_category_data),
)

@DrawableRes
fun categoryIconRes(category: String): Int = when (category) {
    Category.VISUAL -> R.drawable.ic_category_visual
    Category.DATA -> R.drawable.ic_category_data
    else -> R.drawable.ic_category_academic
}

fun categoryLabel(category: String): String = when (category) {
    Category.VISUAL -> "Desain"
    Category.DATA -> "Data"
    else -> "Akademik"
}

@Composable
@ReadOnlyComposable
fun categoryColors(category: String): CategoryColors {
    val colors = LocalBerimaColors.current
    return when (category) {
        Category.VISUAL -> colors.categoryVisual
        Category.DATA -> colors.categoryData
        else -> colors.categoryAcademic
    }
}
