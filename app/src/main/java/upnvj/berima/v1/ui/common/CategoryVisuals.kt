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
    @DrawableRes val iconRes: Int?,
    val fullLabel: String = label
)

/** Ordered list for the Home category rail. `null` id = "Semua" (all), shown text-only. */
val categoryVisuals: List<CategoryVisual> = listOf(
    CategoryVisual(null, "Semua", null),
    CategoryVisual(Category.ACADEMIC, "Akademik", R.drawable.ic_category_academic, AppStrings.CATEGORY_ACADEMIC),
    CategoryVisual(Category.VISUAL, "Desain", R.drawable.ic_category_visual, AppStrings.CATEGORY_VISUAL),
    CategoryVisual(Category.DATA, "Data", R.drawable.ic_category_data, AppStrings.CATEGORY_DATA),
)

/** Concrete (selectable) categories only, excluding the "Semua" sentinel. Used by the category picker. */
val selectableCategoryVisuals: List<CategoryVisual> = categoryVisuals.filter { it.id != null }

/** Full Bahasa/English category name for a category id, e.g. "Visual Branding". */
fun categoryFullLabel(category: String): String =
    (selectableCategoryVisuals.firstOrNull { it.id == category } ?: fallbackVisual).fullLabel

private val fallbackVisual: CategoryVisual
    get() = categoryVisuals.first { it.id != null }

@DrawableRes
fun categoryIconRes(category: String): Int =
    (categoryVisuals.firstOrNull { it.id == category } ?: fallbackVisual).iconRes
        ?: fallbackVisual.iconRes!!

fun categoryLabel(category: String): String =
    (categoryVisuals.firstOrNull { it.id == category } ?: fallbackVisual).label

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
