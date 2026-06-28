package upnvj.berima.v1.ui.listing

import upnvj.berima.v1.ui.common.formatRupiah
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Review
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.VerificationBadgeRow
import upnvj.berima.v1.ui.common.categoryThumbnailRes
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ListingDetailScreen(
    onNavigateBack: () -> Unit,
    onOrderClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onSellerClick: (String) -> Unit,
    viewModel: ListingDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val listing by viewModel.listing.collectAsStateWithLifecycle()
    val seller by viewModel.seller.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val berimaColors = LocalBerimaColors.current
    var showDeactivateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(success) {
        success?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccess()
        }
    }

    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = { Text(AppStrings.LISTING_DEACTIVATE_TITLE) },
            text = { Text(AppStrings.LISTING_DEACTIVATE_BODY) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeactivateDialog = false
                        viewModel.deactivateListing()
                    }
                ) {
                    Text(AppStrings.LISTING_DEACTIVATE_CONFIRM)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) {
                    Text(AppStrings.LISTING_DEACTIVATE_CANCEL)
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = AppStrings.BACK_CONTENT_DESCRIPTION,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = berimaColors.surfaceRaised
                )
            )
        },
        modifier = modifier
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            listing == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = AppStrings.LISTING_NOT_FOUND,
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                val l = listing!!
                val isOwner = viewModel.currentUserId == l.sellerId
                val sellerUser = seller

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        if (l.thumbnailUrl != null) {
                            AsyncImage(
                                model = l.thumbnailUrl,
                                contentDescription = l.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            androidx.compose.foundation.Image(
                                painter = painterResource(categoryThumbnailRes(l.category)),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = l.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = formatRupiah(l.price),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "${AppStrings.LISTING_ESTIMATE_PREFIX} ${l.deliveryTimeHours} jam",
                            style = MaterialTheme.typography.bodyMedium,
                            color = berimaColors.textSecondary
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = l.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (l.tags.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                l.tags.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = tag.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = AppStrings.ORDERS_ROLE_SELLER_PREFIX,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, berimaColors.borderSubtle, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = l.sellerPhotoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = l.sellerName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_star),
                                        contentDescription = null,
                                        tint = berimaColors.starRating,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = if (l.sellerRating > 0.0)
                                            String.format(Locale.US, "%.1f", l.sellerRating)
                                        else "Belum ada rating",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = berimaColors.textSecondary
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "· ${l.totalOrders} pesanan",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = berimaColors.textSecondary
                                    )
                                }
                                VerificationBadgeRow(
                                    isIdentityVerified = sellerUser?.isIdentityVerified
                                        ?: l.sellerIdentityVerified,
                                    skillBadges = sellerUser?.verifiedSkillBadges
                                        ?: l.sellerVerifiedSkillBadges,
                                    relevantSkillCategory = l.category,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            IconButton(onClick = { onSellerClick(l.sellerId) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_back),
                                    contentDescription = "Lihat profil",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (reviews.isNotEmpty()) {
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "Ulasan",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(12.dp))
                            reviews.take(5).forEach { review ->
                                ReviewItem(review = review)
                                Spacer(Modifier.height(12.dp))
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        if (isOwner) {
                            BerimaButton(
                                text = AppStrings.LISTING_EDIT_ACTION,
                                onClick = { onEditClick(l.listingId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (l.isActive) {
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = AppStrings.LISTING_DEACTIVATE,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .clickable { showDeactivateDialog = true }
                                        .align(Alignment.CenterHorizontally)
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            } else {
                                Spacer(Modifier.height(10.dp))
                                InactiveListingNotice(modifier = Modifier.fillMaxWidth())
                            }
                        } else if (l.isActive) {
                            BerimaButton(
                                text = AppStrings.LISTING_ORDER_ACTION,
                                onClick = { onOrderClick(l.listingId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            InactiveListingNotice(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InactiveListingNotice(modifier: Modifier = Modifier) {
    val berimaColors = LocalBerimaColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = AppStrings.LISTING_INACTIVE_LABEL,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = AppStrings.LISTING_INACTIVE_BODY,
            style = MaterialTheme.typography.bodyMedium,
            color = berimaColors.textSecondary
        )
    }
}

@Composable
private fun ReviewItem(
    review: Review,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = review.buyerPhotoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = review.buyerName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(review.rating.coerceIn(0, 5)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_star),
                            contentDescription = null,
                            tint = berimaColors.starRating,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
        if (!review.comment.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}



@androidx.compose.ui.tooling.preview.Preview(name = "ListingDetailScreen · buyer view", showBackground = true, showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ListingDetailScreenPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        val berimaColors = LocalBerimaColors.current
        val listing = upnvj.berima.v1.data.model.Listing(
            listingId = "1",
            sellerId = "seller1",
            sellerName = "Andi Pratama",
            sellerRating = 4.8,
            title = "Desain Logo Profesional untuk Brand Kamu",
            description = "Saya akan membuat desain logo profesional yang sesuai dengan identitas brand kamu. Termasuk revisi hingga 3 kali.",
            category = upnvj.berima.v1.data.model.Category.VISUAL,
            price = 75000L,
            deliveryTimeHours = 24,
            tags = listOf("desain", "logo", "branding"),
            averageRating = 4.8,
            totalOrders = 12
        )
        val reviews = listOf(
            upnvj.berima.v1.data.model.Review(
                reviewId = "r1",
                buyerName = "Budi Santoso",
                rating = 5,
                comment = "Hasilnya sangat memuaskan, cepat dan profesional!"
            )
        )
        androidx.compose.material3.Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = {},
                    navigationIcon = {
                        androidx.compose.material3.IconButton(onClick = {}) {
                            Icon(painter = androidx.compose.ui.res.painterResource(upnvj.berima.v1.R.drawable.ic_arrow_back), contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = berimaColors.surfaceRaised)
                )
            }
        ) { padding ->
            androidx.compose.foundation.rememberScrollState().let { scroll ->
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scroll)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(220.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painter = androidx.compose.ui.res.painterResource(upnvj.berima.v1.R.drawable.ic_berima_mark), contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                    }
                    androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp)) {
                        Text(listing.title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(8.dp))
                        Text(formatRupiah(listing.price), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(4.dp))
                        Text("Estimasi selesai: ${listing.deliveryTimeHours} jam", style = MaterialTheme.typography.bodyMedium, color = berimaColors.textSecondary)
                        Spacer(Modifier.height(16.dp))
                        Text(listing.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(24.dp))
                        reviews.forEach { review -> ReviewItem(review = review); Spacer(Modifier.height(12.dp)) }
                        Spacer(Modifier.height(24.dp))
                        BerimaButton(text = "Pesan Sekarang", onClick = {}, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
