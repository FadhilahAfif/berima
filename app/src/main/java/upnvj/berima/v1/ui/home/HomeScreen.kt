package upnvj.berima.v1.ui.home

import upnvj.berima.v1.ui.common.AppStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.ui.common.ListingCard
import upnvj.berima.v1.ui.common.categoryVisuals
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@Composable
fun HomeScreen(
    onListingClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val featuredListings by viewModel.featuredListings.collectAsStateWithLifecycle()
    val listings by viewModel.listings.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isListLoading by viewModel.isListLoading.collectAsStateWithLifecycle()
    val firstName by viewModel.userFirstName.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
        modifier = modifier
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header band: greeting + search, on a warm raised surface.
            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeHeader(
                    firstName = firstName,
                    onSearchClick = onSearchClick
                )
            }

            // "Sedang ramai" horizontal rail. Hidden when empty.
            if (featuredListings.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        Spacer(Modifier.height(20.dp))
                        SectionHeader(
                            title = AppStrings.HOME_SECTION_FEATURED,
                            subtitle = AppStrings.HOME_SECTION_FEATURED_SUB,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(14.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(featuredListings, key = { it.listingId }) { listing ->
                                ListingCard(
                                    listing = listing,
                                    onClick = { onListingClick(listing.listingId) },
                                    modifier = Modifier.width(196.dp),
                                    imageHeight = 108.dp
                                )
                            }
                        }
                    }
                }
            }

            // "Terbaru" section header + category rail.
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(Modifier.height(24.dp))
                    SectionHeader(
                        title = AppStrings.HOME_SECTION_LATEST,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    CategoryRail(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.selectCategory(it) }
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }

            when {
                isListLoading && listings.isEmpty() -> {
                    items(4) { index ->
                        ListingSkeleton(
                            modifier = Modifier.padding(
                                start = if (index % 2 == 0) 16.dp else 0.dp,
                                end = if (index % 2 == 0) 0.dp else 16.dp
                            )
                        )
                    }
                }

                listings.isEmpty() -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyListings(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 56.dp)
                        )
                    }
                }

                else -> {
                    itemsIndexed(listings, key = { _, item -> item.listingId }) { index, listing ->
                        ListingCard(
                            listing = listing,
                            onClick = { onListingClick(listing.listingId) },
                            modifier = Modifier.padding(
                                start = if (index % 2 == 0) 16.dp else 0.dp,
                                end = if (index % 2 == 0) 0.dp else 16.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    firstName: String,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(berimaColors.surfaceRaised)
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_berima_mark),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = if (firstName.isNotBlank())
                        String.format(AppStrings.HOME_GREETING_NAMED, firstName)
                    else AppStrings.HOME_GREETING_GENERIC,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = AppStrings.HOME_SUBTITLE,
                    style = MaterialTheme.typography.bodyMedium,
                    color = berimaColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(9999.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, berimaColors.borderInput, RoundedCornerShape(9999.dp))
                .clickable(onClick = onSearchClick)
                .padding(horizontal = 18.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = berimaColors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = AppStrings.SEARCH_PLACEHOLDER,
                style = MaterialTheme.typography.bodyLarge,
                color = berimaColors.textSecondary
            )
        }
    }
}

@Composable
private fun CategoryRail(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(categoryVisuals, key = { it.label }) { cat ->
            val isSelected = selectedCategory == cat.id
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else berimaColors.borderSubtle,
                        shape = RoundedCornerShape(9999.dp)
                    )
                    .clickable { onCategorySelected(if (isSelected && cat.id != null) null else cat.id) }
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (cat.iconRes != null) {
                    Icon(
                        painter = painterResource(cat.iconRes),
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = cat.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = LocalBerimaColors.current.textSecondary
            )
        }
    }
}

@Composable
private fun ListingSkeleton(modifier: Modifier = Modifier) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .height(304.dp)
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .fillMaxWidth(0.85f)
                .height(14.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .fillMaxWidth(0.5f)
                .height(20.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun EmptyListings(modifier: Modifier = Modifier) {
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
        Spacer(Modifier.height(16.dp))
        Text(
            text = AppStrings.HOME_EMPTY_TITLE,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = AppStrings.HOME_EMPTY_BODY,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
