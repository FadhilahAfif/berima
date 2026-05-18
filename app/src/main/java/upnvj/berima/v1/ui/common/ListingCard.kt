package upnvj.berima.v1.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ListingCard(
    listing: Listing,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageHeight: Dp = 120.dp
) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .clickable(onClick = onClick)
            .padding(bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            if (listing.thumbnailUrl != null) {
                AsyncImage(
                    model = listing.thumbnailUrl,
                    contentDescription = listing.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_berima_mark),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text = listing.title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = formatRupiah(listing.price),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = listing.sellerPhotoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = listing.sellerName,
                    style = MaterialTheme.typography.bodySmall,
                    color = berimaColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = null,
                    tint = berimaColors.starRating,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = if (listing.averageRating > 0.0)
                        String.format(Locale.US, "%.1f", listing.averageRating)
                    else "-",
                    style = MaterialTheme.typography.bodySmall,
                    color = berimaColors.textSecondary
                )
            }
        }
    }
}

private fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp${formatter.format(amount)}"
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
            modifier = androidx.compose.ui.Modifier.padding(16.dp)
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
            modifier = androidx.compose.ui.Modifier.padding(16.dp)
        )
    }
}
