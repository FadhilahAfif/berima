package upnvj.berima.v1.ui.common

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.util.Locale

@Composable
fun ListingCard(
    listing: Listing,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageHeight: Dp = 124.dp,
    cardHeight: Dp = 304.dp
) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(16.dp)
    val hasRating = listing.averageRating > 0.0

    Column(
        modifier = modifier
            .height(cardHeight)
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        ListingThumbnail(
            listing = listing,
            imageHeight = imageHeight
        )

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = listing.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = formatRupiah(listing.price),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            ListingSkillBadge(
                category = listing.category,
                verifiedSkillBadges = listing.sellerVerifiedSkillBadges,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = listing.sellerPhotoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = listing.sellerName,
                    style = MaterialTheme.typography.bodySmall,
                    color = berimaColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (hasRating) {
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        tint = berimaColors.starRating,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = String.format(Locale("id", "ID"), "%.1f", listing.averageRating),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

/**
 * Thumbnail region. When [Listing.thumbnailUrl] is present it is cropped to fill.
 * Otherwise a category-tinted placeholder is drawn with the category glyph and a
 * label chip, so listings without an uploaded image still look intentional.
 */
@Composable
private fun ListingThumbnail(
    listing: Listing,
    imageHeight: Dp
) {
    val thumbShape = RoundedCornerShape(12.dp)
    val category = categoryColors(listing.category)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageHeight)
            .clip(thumbShape)
            .background(if (listing.thumbnailUrl != null) MaterialTheme.colorScheme.surfaceContainerHigh else category.container)
    ) {
        if (listing.thumbnailUrl != null) {
            AsyncImage(
                model = listing.thumbnailUrl,
                contentDescription = listing.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            androidx.compose.foundation.Image(
                painter = painterResource(categoryThumbnailRes(listing.category)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            CategoryTag(
                label = categoryLabel(listing.category),
                glyph = category.glyph,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun CategoryTag(
    label: String,
    glyph: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.82f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = glyph
        )
    }
}


@androidx.compose.ui.tooling.preview.Preview(name = "ListingCard · with rating", showBackground = true, backgroundColor = 0xFFF2EFE9)
@Composable
private fun ListingCardPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        ListingCard(
            listing = upnvj.berima.v1.data.model.Listing(
                listingId = "1",
                title = "Desain Logo Profesional untuk Brand Kamu",
                sellerName = "Andi Pratama",
                sellerRating = 4.8,
                price = 75000L,
                averageRating = 4.8,
                totalOrders = 12,
                category = upnvj.berima.v1.data.model.Category.VISUAL
            ),
            onClick = {},
            modifier = androidx.compose.ui.Modifier
                .width(200.dp)
                .padding(16.dp)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "ListingCard · no rating", showBackground = true, backgroundColor = 0xFFF2EFE9)
@Composable
private fun ListingCardNoRatingPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        ListingCard(
            listing = upnvj.berima.v1.data.model.Listing(
                listingId = "2",
                title = "Analisis Data Statistik Tugas Akhir",
                sellerName = "Siti Rahayu",
                sellerRating = 0.0,
                price = 50000L,
                averageRating = 0.0,
                totalOrders = 0,
                category = upnvj.berima.v1.data.model.Category.DATA
            ),
            onClick = {},
            modifier = androidx.compose.ui.Modifier
                .width(200.dp)
                .padding(16.dp)
        )
    }
}
