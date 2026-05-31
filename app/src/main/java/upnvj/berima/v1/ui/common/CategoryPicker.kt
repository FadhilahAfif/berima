package upnvj.berima.v1.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Category
import upnvj.berima.v1.ui.theme.LocalBerimaColors

/**
 * Shared category selector for the listing forms (CreateListing + EditListing).
 *
 * Renders a tappable field styled to match [BerimaTextField] (white surface, 12dp
 * radius, forest hairline, floating-style label) that shows the selected category's
 * glyph + full name. Tapping it opens [CategorySheet] — the canonical bottom-sheet
 * picker from DESIGN.md (surface-raised, 16dp top corners, handle bar, scrim).
 *
 * This replaces the old `ExposedDropdownMenu`-based `CategoryDropdown`. Prefer this
 * component for any "pick one of the listing categories" need so the affordance and
 * glyphs stay consistent across screens. Selection state is owned by the caller's
 * ViewModel; this component is stateless apart from the sheet's open/closed flag.
 *
 * @param selected the currently selected [Category] id (e.g. [Category.VISUAL]).
 * @param onSelected invoked with the chosen category id when the user picks one.
 * @param label field label, defaults to the shared "Kategori" string.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerField(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = AppStrings.LISTING_FIELD_CATEGORY
) {
    val berimaColors = LocalBerimaColors.current
    var sheetOpen by remember { mutableStateOf(false) }
    val category = categoryColors(selected)

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = berimaColors.textSecondary
        )
        Spacer(Modifier.size(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.5.dp, berimaColors.borderInput, RoundedCornerShape(12.dp))
                .clickable { sheetOpen = true }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(category.container),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(categoryIconRes(selected)),
                    contentDescription = null,
                    tint = category.glyph,
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = categoryFullLabel(selected),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = null,
                tint = berimaColors.textSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }

    if (sheetOpen) {
        CategorySheet(
            selected = selected,
            onSelected = {
                onSelected(it)
                sheetOpen = false
            },
            onDismiss = { sheetOpen = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySheet(
    selected: String,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val berimaColors = LocalBerimaColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = berimaColors.surfaceRaised,
        dragHandle = {
            Box(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 4.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = AppStrings.CATEGORY_SHEET_TITLE,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.size(16.dp))
            selectableCategoryVisuals.forEach { cat ->
                val id = cat.id ?: return@forEach
                CategorySheetRow(
                    categoryId = id,
                    label = cat.fullLabel,
                    isSelected = id == selected,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            onSelected(id)
                        }
                    }
                )
                Spacer(Modifier.size(10.dp))
            }
        }
    }
}

@Composable
private fun CategorySheetRow(
    categoryId: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val category = categoryColors(categoryId)
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else berimaColors.borderSubtle,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(category.container),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(categoryIconRes(categoryId)),
                contentDescription = null,
                tint = category.glyph,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "CategoryPickerField", showBackground = true, backgroundColor = 0xFFF2EFE9)
@Composable
private fun CategoryPickerFieldPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        CategoryPickerField(
            selected = Category.VISUAL,
            onSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
