package upnvj.berima.v1.ui.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.data.model.User
import upnvj.berima.v1.data.model.UserRole
import upnvj.berima.v1.data.model.VerificationStatus
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.categoryColors
import upnvj.berima.v1.ui.common.categoryIconRes
import upnvj.berima.v1.ui.common.formatRupiah
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToCreateListing: () -> Unit,
    onNavigateToVerificationCenter: () -> Unit,
    onListingClick: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val listings by viewModel.listings.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val berimaColors = LocalBerimaColors.current

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.PROFILE_TITLE,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    LogoutButton(
                        onClick = {
                            viewModel.signOut()
                            onLogout()
                        },
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = berimaColors.surfaceRaised
                )
            )
        },
        modifier = modifier
    ) { padding ->
        when {
            isLoading && user == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            user == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = AppStrings.PROFILE_NOT_FOUND,
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                ProfileContent(
                    user = user!!,
                    listings = listings,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToCreateListing = onNavigateToCreateListing,
                    onNavigateToVerificationCenter = onNavigateToVerificationCenter,
                    onListingClick = onListingClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }
}

@Composable
private fun LogoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderInput, RoundedCornerShape(9999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = AppStrings.PROFILE_LOGOUT,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileContent(
    user: User,
    listings: List<Listing>,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToCreateListing: () -> Unit,
    onNavigateToVerificationCenter: () -> Unit,
    onListingClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        IdentityCard(user = user, onEditClick = onNavigateToEditProfile)

        if (shouldShowStats(user)) {
            Spacer(Modifier.height(12.dp))
            StatsStrip(user = user)
        }

        Spacer(Modifier.height(16.dp))

        VerificationEntryCard(
            user = user,
            onClick = onNavigateToVerificationCenter
        )

        Spacer(Modifier.height(16.dp))

        BerimaButton(
            text = AppStrings.PROFILE_ADD_LISTING,
            onClick = onNavigateToCreateListing,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(28.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = AppStrings.PROFILE_MY_LISTINGS,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (listings.isNotEmpty()) {
                Text(
                    text = "${listings.size}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = LocalBerimaColors.current.textSecondary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (listings.isEmpty()) {
            EmptyListingState(
                onCreate = onNavigateToCreateListing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listings.forEach { listing ->
                    ProfileListingRow(
                        listing = listing,
                        onClick = { onListingClick(listing.listingId) }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun IdentityCard(
    user: User,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(photoUrl = user.photoUrl, size = 64.dp)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = berimaColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                RoleChip(role = user.role)
            }
            EditIconButton(onClick = onEditClick)
        }

        if (!user.bio.isNullOrBlank()) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (!user.faculty.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_academic),
                    contentDescription = null,
                    tint = berimaColors.textSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = user.faculty,
                    style = MaterialTheme.typography.bodyMedium,
                    color = berimaColors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun EditIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(berimaColors.surfaceRaised)
            .border(1.dp, berimaColors.borderSubtle, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_edit),
            contentDescription = AppStrings.PROFILE_EDIT,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ProfileAvatar(
    photoUrl: String?,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 64.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl.isNullOrBlank()) {
            Icon(
                painter = painterResource(R.drawable.ic_person),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(size * 0.45f)
            )
        } else {
            AsyncImage(
                model = photoUrl,
                contentDescription = AppStrings.PROFILE_PHOTO_DESCRIPTION,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun RoleChip(
    role: String,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val label = when (role) {
        UserRole.BUYER -> AppStrings.ROLE_BUYER.uppercase()
        UserRole.SELLER -> AppStrings.ROLE_SELLER.uppercase()
        else -> AppStrings.ROLE_BOTH.uppercase()
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(berimaColors.containerGreen)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun VerificationEntryCard(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(berimaColors.containerGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = AppStrings.PROFILE_VERIFICATION_TITLE,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = AppStrings.PROFILE_VERIFICATION_BODY.format(
                    verificationStatusLabel(user.identityVerificationStatus),
                    skillStatusLabel(user.verifiedSkillBadges.size)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = berimaColors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = AppStrings.PROFILE_VERIFICATION_ACTION,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(6.dp))
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun StatsStrip(
    user: User,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val isSeller = user.role == UserRole.SELLER || user.role == UserRole.BOTH
    val showRating = isSeller && user.averageRating > 0.0

    val stats = buildList {
        if (showRating) {
            add(StatItem(String.format(Locale("id", "ID"), "%.1f", user.averageRating), AppStrings.PROFILE_STAT_RATING, isRating = true))
        }
        if (user.totalOrdersAsSeller > 0) {
            add(StatItem("${user.totalOrdersAsSeller}", AppStrings.PROFILE_STAT_AS_SELLER))
        }
        if (user.totalOrdersAsBuyer > 0) {
            add(StatItem("${user.totalOrdersAsBuyer}", AppStrings.PROFILE_STAT_AS_BUYER))
        }
    }

    if (stats.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        stats.forEachIndexed { index, stat ->
            StatColumn(stat = stat, modifier = Modifier.weight(1f))
            if (index < stats.lastIndex) {
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(berimaColors.borderSubtle)
                )
            }
        }
    }
}

private data class StatItem(val value: String, val label: String, val isRating: Boolean = false)

@Composable
private fun StatColumn(
    stat: StatItem,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (stat.isRating) {
                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = null,
                    tint = berimaColors.starRating,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = stat.value,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = stat.label,
            style = MaterialTheme.typography.labelSmall,
            color = berimaColors.textSecondary
        )
    }
}

@Composable
private fun ProfileListingRow(
    listing: Listing,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val hasRating = listing.averageRating > 0.0
    val category = categoryColors(listing.category)
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, shape)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (listing.thumbnailUrl != null) MaterialTheme.colorScheme.surfaceContainerHigh else category.container),
            contentAlignment = Alignment.Center
        ) {
            if (listing.thumbnailUrl != null) {
                AsyncImage(
                    model = listing.thumbnailUrl,
                    contentDescription = listing.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter = painterResource(categoryIconRes(listing.category)),
                    contentDescription = null,
                    tint = category.glyph,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = listing.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatRupiah(listing.price),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (hasRating) {
                    Spacer(Modifier.width(10.dp))
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

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = berimaColors.textSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun EmptyListingState(
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_berima_mark),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(44.dp)
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = AppStrings.PROFILE_EMPTY_LISTINGS_TITLE,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = AppStrings.PROFILE_EMPTY_LISTINGS_BODY,
            style = MaterialTheme.typography.bodyMedium,
            color = berimaColors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

private fun shouldShowStats(user: User): Boolean {
    val showSellerRating = (user.role == UserRole.SELLER || user.role == UserRole.BOTH) && user.averageRating > 0.0
    return showSellerRating || user.totalOrdersAsBuyer > 0 || user.totalOrdersAsSeller > 0
}

private fun verificationStatusLabel(status: String): String = when (status) {
    VerificationStatus.PENDING -> AppStrings.VERIFICATION_STATUS_PENDING
    VerificationStatus.APPROVED -> AppStrings.VERIFICATION_STATUS_APPROVED
    VerificationStatus.REJECTED -> AppStrings.VERIFICATION_STATUS_REJECTED
    else -> AppStrings.VERIFICATION_STATUS_NOT_SUBMITTED
}

private fun skillStatusLabel(activeBadgeCount: Int): String {
    return if (activeBadgeCount > 0) {
        AppStrings.VERIFICATION_SKILL_ACTIVE.format(activeBadgeCount)
    } else {
        AppStrings.VERIFICATION_SKILL_NONE
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "ProfileScreen", showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        val user = User(
            uid = "u1",
            name = "Andi Pratama",
            email = "test+seller@berima.dev",
            bio = "Jasa desain PPT dan poster UKM profesional.",
            faculty = "Teknik Informatika",
            role = UserRole.BOTH,
            averageRating = 4.8,
            totalOrdersAsBuyer = 3,
            totalOrdersAsSeller = 15
        )
        val listings = listOf(
            Listing(
                listingId = "l1",
                title = "Desain PPT Presentasi Sidang",
                sellerName = "Andi Pratama",
                sellerId = "u1",
                price = 45000,
                averageRating = 4.8,
                category = "visual"
            ),
            Listing(
                listingId = "l2",
                title = "Bantu Olah Data SPSS Tugas Akhir",
                sellerName = "Andi Pratama",
                sellerId = "u1",
                price = 65000,
                averageRating = 0.0,
                category = "data"
            )
        )

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0)
        ) { padding ->
            ProfileContent(
                user = user,
                listings = listings,
                onNavigateToEditProfile = {},
                onNavigateToCreateListing = {},
                onNavigateToVerificationCenter = {},
                onListingClick = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}
