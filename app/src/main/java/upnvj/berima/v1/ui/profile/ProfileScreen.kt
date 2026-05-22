package upnvj.berima.v1.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.data.model.User
import upnvj.berima.v1.data.model.UserRole
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.ListingCard
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToCreateListing: () -> Unit,
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
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Profil",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.signOut()
                                onLogout()
                            }
                        ) {
                            Text(
                                text = "Keluar",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = berimaColors.surfaceRaised
                    )
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = berimaColors.borderSubtle
                )
            }
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
                        text = "Profil tidak ditemukan",
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
private fun ProfileContent(
    user: User,
    listings: List<Listing>,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToCreateListing: () -> Unit,
    onListingClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(photoUrl = user.photoUrl)
            androidx.compose.foundation.layout.Spacer(Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = berimaColors.textSecondary
                )
            }
        }

        if (!user.bio.isNullOrBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (!user.faculty.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = user.faculty,
                style = MaterialTheme.typography.bodySmall,
                color = berimaColors.textSecondary
            )
        }

        Spacer(Modifier.height(16.dp))
        RoleBadge(role = user.role)

        if (shouldShowStats(user)) {
            Spacer(Modifier.height(16.dp))
            StatsSection(user = user)
        }

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            androidx.compose.material3.OutlinedButton(
                onClick = onNavigateToEditProfile,
                shape = RoundedCornerShape(9999.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Edit Profil",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            BerimaButton(
                text = "Tambah Listing Baru",
                onClick = onNavigateToCreateListing,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = berimaColors.borderSubtle, thickness = 1.dp)
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Listing Saya",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(16.dp))

        if (listings.isEmpty()) {
            EmptyListingState(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listings.forEach { listing ->
                    ListingCard(
                        listing = listing,
                        onClick = { onListingClick(listing.listingId) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    photoUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl.isNullOrBlank()) {
            Icon(
                painter = painterResource(R.drawable.ic_person),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(36.dp)
            )
        } else {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Foto profil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun RoleBadge(
    role: String,
    modifier: Modifier = Modifier
) {
    val label = when (role) {
        UserRole.BUYER -> "PEMBELI"
        UserRole.SELLER -> "PENJUAL"
        else -> "KEDUANYA"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun StatsSection(
    user: User,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if ((user.role == UserRole.SELLER || user.role == UserRole.BOTH) && user.averageRating > 0.0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = null,
                    tint = berimaColors.starRating,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = String.format(Locale.US, "%.1f", user.averageRating),
                    style = MaterialTheme.typography.bodySmall,
                    color = berimaColors.textSecondary
                )
            }
        }

        if (user.totalOrdersAsBuyer > 0) {
            Text(
                text = "${user.totalOrdersAsBuyer} pesanan sebagai pembeli",
                style = MaterialTheme.typography.bodySmall,
                color = berimaColors.textSecondary
            )
        }

        if (user.totalOrdersAsSeller > 0) {
            Text(
                text = "${user.totalOrdersAsSeller} pesanan sebagai penjual",
                style = MaterialTheme.typography.bodySmall,
                color = berimaColors.textSecondary
            )
        }
    }
}

@Composable
private fun EmptyListingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_berima_mark),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Belum ada listing",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Tambah listing pertamamu",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun shouldShowStats(user: User): Boolean {
    val showSellerRating = (user.role == UserRole.SELLER || user.role == UserRole.BOTH) && user.averageRating > 0.0
    return showSellerRating || user.totalOrdersAsBuyer > 0 || user.totalOrdersAsSeller > 0
}

@androidx.compose.ui.tooling.preview.Preview(name = "ProfileScreen", showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        val user = User(
            uid = "u1",
            name = "Andi Pratama",
            email = "andi@upnvj.ac.id",
            bio = "Mahasiswa Informatika yang suka bantu desain dan data.",
            faculty = "Fakultas Ilmu Komputer",
            role = UserRole.BOTH,
            averageRating = 4.8,
            totalOrdersAsBuyer = 3,
            totalOrdersAsSeller = 12
        )
        val listings = listOf(
            Listing(
                listingId = "l1",
                title = "Desain Poster Acara Kampus",
                sellerName = "Andi Pratama",
                sellerId = "u1",
                price = 45000,
                averageRating = 4.8,
                category = "visual"
            ),
            Listing(
                listingId = "l2",
                title = "Bantu Olah Data SPSS",
                sellerName = "Andi Pratama",
                sellerId = "u1",
                price = 65000,
                averageRating = 4.7,
                category = "data"
            )
        )

        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            ProfileContent(
                user = user,
                listings = listings,
                onNavigateToEditProfile = {},
                onNavigateToCreateListing = {},
                onListingClick = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}
