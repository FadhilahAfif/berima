package upnvj.berima.v1.ui.review

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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Order
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.ui.common.BerimaButton
import upnvj.berima.v1.ui.common.BerimaTextField
import upnvj.berima.v1.ui.theme.BerimaTheme
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateReviewViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val order by viewModel.order.collectAsStateWithLifecycle()
    val rating by viewModel.rating.collectAsStateWithLifecycle()
    val comment by viewModel.comment.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isSubmitted by viewModel.isSubmitted.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val berimaColors = LocalBerimaColors.current

    LaunchedEffect(isSubmitted) {
        if (isSubmitted) onNavigateBack()
    }

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
                        text = "Tulis Ulasan",
                        style = MaterialTheme.typography.titleMedium,
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
            isLoading && order == null -> {
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

            order == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pesanan tidak ditemukan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                CreateReviewContent(
                    order = order!!,
                    rating = rating,
                    comment = comment,
                    isLoading = isLoading,
                    onRatingChange = viewModel::onRatingChange,
                    onCommentChange = viewModel::onCommentChange,
                    onSubmit = viewModel::submit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }
}

@Composable
private fun CreateReviewContent(
    order: Order,
    rating: Int,
    comment: String,
    isLoading: Boolean,
    onRatingChange: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        OrderSummaryCard(order = order, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Rating",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        StarRatingSelector(
            rating = rating,
            onRatingChange = onRatingChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        BerimaTextField(
            value = comment,
            onValueChange = onCommentChange,
            label = "Komentar (opsional)",
            singleLine = false,
            maxLines = 5,
            supportingText = {
                Text(
                    text = "${comment.length}/${Validation.MAX_REVIEW_COMMENT_LENGTH}",
                    style = MaterialTheme.typography.labelSmall,
                    color = berimaColors.textSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        BerimaButton(
            text = "Kirim Ulasan",
            onClick = onSubmit,
            isLoading = isLoading,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun OrderSummaryCard(
    order: Order,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, shape)
            .padding(12.dp)
    ) {
        Text(
            text = order.listingTitle,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = order.sellerName,
            style = MaterialTheme.typography.bodySmall,
            color = berimaColors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StarRatingSelector(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    Row(modifier = modifier) {
        (1..5).forEach { index ->
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = "Rating $index",
                tint = if (index <= rating) berimaColors.starRating else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingChange(index) }
            )
            if (index < 5) {
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "CreateReviewScreen", showBackground = true, showSystemUi = true)
@Composable
private fun CreateReviewScreenPreview() {
    BerimaTheme {
        val berimaColors = LocalBerimaColors.current
        val sampleOrder = Order(
            orderId = "order1",
            listingId = "listing1",
            listingTitle = "Jasa Desain Poster UKM",
            buyerId = "buyer1",
            buyerName = "Budi",
            sellerId = "seller1",
            sellerName = "Andi",
            status = "paid",
            hasReview = false
        )

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Tulis Ulasan",
                            style = MaterialTheme.typography.titleMedium,
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
            CreateReviewContent(
                order = sampleOrder,
                rating = 4,
                comment = "Hasil bagus dan cepat",
                isLoading = false,
                onRatingChange = {},
                onCommentChange = {},
                onSubmit = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}
