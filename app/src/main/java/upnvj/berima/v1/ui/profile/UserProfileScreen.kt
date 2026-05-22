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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Listing
import upnvj.berima.v1.data.model.User
import upnvj.berima.v1.ui.common.ListingCard
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit,
    onListingClick: (String) -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel(),
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
            TopAppBar(
                title = {
                    Text(
                        text = "Profil Penjual",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Kembali",
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
                        text = "Pengguna tidak ditemukan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                UserProfileContent(
                    user = user!!,
                    listings = listings,
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
private fun UserProfileContent(
    user: User,
    listings: List<Listing>,
    onListingClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                if (!user.photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = user.name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            if (!user.bio.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            if (!user.faculty.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = user.faculty,
                    style = MaterialTheme.typography.bodySmall,
                    color = berimaColors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (user.averageRating > 0.0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_star),
                            contentDescription = null,
                            tint = berimaColors.starRating,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f", user.averageRating),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "${user.totalReviews} ulasan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = berimaColors.textSecondary
                )

                Text(
                    text = "${user.totalOrdersAsSeller} pesanan selesai",
                    style = MaterialTheme.typography.bodyMedium,
                    color = berimaColors.textSecondary
                )
            }
        }

        HorizontalDivider(color = berimaColors.borderSubtle)

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Listing Aktif",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(12.dp))

            if (listings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada listing aktif",
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                listings.forEach { listing ->
                    ListingCard(
                        listing = listing,
                        onClick = { onListingClick(listing.listingId) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "UserProfileScreen · with listings",
    showBackground = true,
    showSystemUi = true
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserProfileScreenPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        val berimaColors = LocalBerimaColors.current
        val user = User(
            uid = "u1",
            name = "Andi Pratama",
            bio = "Desainer grafis dan mahasiswa Teknik Informatika.",
            faculty = "Fakultas Ilmu Komputer",
            averageRating = 4.8,
            totalReviews = 12,
            totalOrdersAsSeller = 20
        )
        val listings = listOf(
            upnvj.berima.v1.data.model.Listing(
                listingId = "l1",
                title = "Desain Logo Profesional",
                sellerName = "Andi Pratama",
                price = 75000L,
                averageRating = 4.8,
                category = upnvj.berima.v1.data.model.Category.VISUAL
            ),
            upnvj.berima.v1.data.model.Listing(
                listingId = "l2",
                title = "Desain PPT Presentasi",
                sellerName = "Andi Pratama",
                price = 50000L,
                averageRating = 4.5,
                category = upnvj.berima.v1.data.model.Category.VISUAL
            )
        )
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Profil Penjual",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = berimaColors.surfaceRaised
                    )
                )
            }
        ) { padding ->
            UserProfileContent(
                user = user,
                listings = listings,
                onListingClick = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}
