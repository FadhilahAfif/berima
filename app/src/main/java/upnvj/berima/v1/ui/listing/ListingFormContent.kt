package upnvj.berima.v1.ui.listing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
import upnvj.berima.v1.ui.common.CategoryPickerField
import upnvj.berima.v1.ui.common.formatRupiahInput
import upnvj.berima.v1.ui.theme.LocalBerimaColors

/**
 * Shared listing form used by both CreateListingScreen and EditListingScreen so the
 * field set, grouping, validation hints, and visual treatment stay identical.
 *
 * Stateless: every value + change lambda is hoisted to the caller's ViewModel. The
 * form groups fields into three labelled sections (Detail, Harga & Waktu, Tambahan)
 * for vertical rhythm, surfaces live character counters on the length-limited fields,
 * gives the price its own Rp-prefixed treatment with a formatted preview (money is the
 * most prominent value per DESIGN.md), and opens the shared [CategoryPickerField]
 * bottom sheet for category selection.
 *
 * @param submitLabel button copy, differs between create ("Simpan Listing") and edit.
 */
@Composable
fun ListingFormContent(
    title: String,
    onTitleChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit,
    deliveryTimeHours: String,
    onDeliveryTimeChange: (String) -> Unit,
    tags: String,
    onTagsChange: (String) -> Unit,
    isLoading: Boolean,
    submitLabel: String,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))

        FormSection(title = AppStrings.LISTING_SECTION_DETAIL) {
            BerimaTextField(
                value = title,
                onValueChange = onTitleChange,
                label = AppStrings.LISTING_FIELD_TITLE,
                placeholder = AppStrings.LISTING_TITLE_PLACEHOLDER,
                supportingText = { CounterText(title.length, Validation.MAX_LISTING_TITLE_LENGTH) }
            )
            Spacer(Modifier.height(14.dp))
            CategoryPickerField(
                selected = category,
                onSelected = onCategoryChange
            )
            Spacer(Modifier.height(14.dp))
            BerimaTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = AppStrings.LISTING_FIELD_DESCRIPTION,
                placeholder = AppStrings.LISTING_DESCRIPTION_PLACEHOLDER,
                singleLine = false,
                maxLines = 6,
                supportingText = {
                    CounterText(description.length, Validation.MAX_LISTING_DESCRIPTION_LENGTH)
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        FormSection(title = AppStrings.LISTING_SECTION_PRICING) {
            BerimaTextField(
                value = price,
                onValueChange = onPriceChange,
                label = AppStrings.LISTING_FIELD_PRICE,
                placeholder = AppStrings.LISTING_PRICE_PLACEHOLDER,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Text(
                        text = "Rp",
                        style = MaterialTheme.typography.headlineSmall,
                        color = berimaColors.textSecondary
                    )
                },
                supportingText = {
                    val preview = formatRupiahInput(price)
                    Text(
                        text = if (preview.isNotBlank()) "Pembeli membayar $preview" else " ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary
                    )
                }
            )
            Spacer(Modifier.height(14.dp))
            BerimaTextField(
                value = deliveryTimeHours,
                onValueChange = onDeliveryTimeChange,
                label = AppStrings.LISTING_FIELD_DELIVERY,
                placeholder = AppStrings.LISTING_DELIVERY_PLACEHOLDER,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Text(
                        text = AppStrings.LISTING_DELIVERY_UNIT,
                        style = MaterialTheme.typography.headlineSmall,
                        color = berimaColors.textSecondary
                    )
                },
                supportingText = {
                    Text(
                        text = "Maksimal ${Validation.MAX_DELIVERY_TIME_HOURS} jam",
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary
                    )
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        FormSection(title = AppStrings.LISTING_SECTION_EXTRA) {
            BerimaTextField(
                value = tags,
                onValueChange = onTagsChange,
                label = AppStrings.LISTING_FIELD_TAGS,
                placeholder = AppStrings.LISTING_TAGS_PLACEHOLDER
            )
        }

        Spacer(Modifier.height(28.dp))

        BerimaButton(
            text = submitLabel,
            onClick = onSubmit,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun FormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(14.dp))
        content()
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
