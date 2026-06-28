package upnvj.berima.v1.ui.listing

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import coil.compose.AsyncImage
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
import upnvj.berima.v1.ui.common.CategoryPickerField
import upnvj.berima.v1.ui.common.CounterText
import upnvj.berima.v1.ui.common.categoryThumbnailRes
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
    selectedThumbnailUri: Uri?,
    existingThumbnailUrl: String?,
    isRemovingExistingThumbnail: Boolean,
    onPickThumbnail: () -> Unit,
    onRemoveThumbnail: () -> Unit,
    isPolicyAccepted: Boolean,
    onPolicyAcceptedChange: (Boolean) -> Unit,
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
                        text = if (preview.isNotBlank()) "${AppStrings.LISTING_PRICE_PREVIEW_PREFIX} $preview" else " ",
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
                        text = "${AppStrings.LISTING_DELIVERY_HINT_PREFIX} ${Validation.MAX_DELIVERY_TIME_HOURS} ${AppStrings.LISTING_DELIVERY_HINT_SUFFIX}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary
                    )
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        FormSection(title = AppStrings.LISTING_SECTION_EXTRA) {
            ListingImagePicker(
                category = category,
                selectedThumbnailUri = selectedThumbnailUri,
                existingThumbnailUrl = existingThumbnailUrl,
                isRemovingExistingThumbnail = isRemovingExistingThumbnail,
                onPickThumbnail = onPickThumbnail,
                onRemoveThumbnail = onRemoveThumbnail
            )
            Spacer(Modifier.height(14.dp))
            BerimaTextField(
                value = tags,
                onValueChange = onTagsChange,
                label = AppStrings.LISTING_FIELD_TAGS,
                placeholder = AppStrings.LISTING_TAGS_PLACEHOLDER
            )
        }

        Spacer(Modifier.height(24.dp))

        PolicyAcknowledgement(
            isChecked = isPolicyAccepted,
            onCheckedChange = onPolicyAcceptedChange
        )

        Spacer(Modifier.height(20.dp))

        BerimaButton(
            text = submitLabel,
            onClick = onSubmit,
            isLoading = isLoading,
            enabled = isPolicyAccepted,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PolicyAcknowledgement(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(berimaColors.surfaceRaised)
            .border(1.dp, berimaColors.borderSubtle, shape)
            .toggleable(
                value = isChecked,
                role = Role.Checkbox,
                onValueChange = onCheckedChange
            )
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = berimaColors.textSecondary
            ),
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = AppStrings.LISTING_POLICY_TITLE,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = AppStrings.LISTING_POLICY_BODY,
                style = MaterialTheme.typography.bodyMedium,
                color = berimaColors.textSecondary
            )
        }
    }
}

@Composable
private fun ListingImagePicker(
    category: String,
    selectedThumbnailUri: Uri?,
    existingThumbnailUrl: String?,
    isRemovingExistingThumbnail: Boolean,
    onPickThumbnail: () -> Unit,
    onRemoveThumbnail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val existingModel = existingThumbnailUrl.takeUnless { isRemovingExistingThumbnail }
    val imageModel: Any? = selectedThumbnailUri ?: existingModel

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(berimaColors.surfaceRaised)
            .border(1.dp, berimaColors.borderSubtle, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = AppStrings.LISTING_IMAGE_TITLE,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                if (imageModel == null) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(categoryThumbnailRes(category)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = AppStrings.LISTING_IMAGE_TITLE,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (imageModel == null) {
                        AppStrings.LISTING_IMAGE_EMPTY
                    } else {
                        AppStrings.LISTING_IMAGE_ATTACHED
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = AppStrings.LISTING_IMAGE_HELP,
                    style = MaterialTheme.typography.bodyMedium,
                    color = berimaColors.textSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Row {
            Text(
                text = if (imageModel == null) {
                    AppStrings.LISTING_PICK_IMAGE
                } else {
                    AppStrings.LISTING_CHANGE_IMAGE
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .clickable(onClick = onPickThumbnail)
                    .padding(vertical = 6.dp)
            )
            if (imageModel != null) {
                Spacer(Modifier.width(12.dp))
                Text(
                    text = AppStrings.LISTING_REMOVE_IMAGE,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .clickable(onClick = onRemoveThumbnail)
                        .padding(vertical = 6.dp)
                )
            }
        }
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
