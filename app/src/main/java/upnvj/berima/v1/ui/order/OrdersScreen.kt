package upnvj.berima.v1.ui.order

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.Timestamp
import upnvj.berima.v1.data.model.Order
import upnvj.berima.v1.data.model.OrderStatus
import upnvj.berima.v1.ui.common.StatusChip
import upnvj.berima.v1.ui.theme.BerimaTheme
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onOrderClick: (String) -> Unit,
    viewModel: OrdersViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val buyerOrders by viewModel.buyerOrders.collectAsStateWithLifecycle()
    val sellerOrders by viewModel.sellerOrders.collectAsStateWithLifecycle()
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
                        text = "Pesanan",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = berimaColors.surfaceRaised
                )
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OrdersTabRow(
                selectedTab = selectedTab,
                onTabSelected = viewModel::selectTab
            )

            val activeOrders = if (selectedTab == 0) buyerOrders else sellerOrders
            val isBuyerView = selectedTab == 0

            if (activeOrders.isEmpty()) {
                EmptyOrders(
                    isBuyerView = isBuyerView,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeOrders, key = { it.orderId }) { order ->
                        OrderRow(
                            order = order,
                            isBuyerView = isBuyerView,
                            onClick = { onOrderClick(order.orderId) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Sebagai Pembeli", "Sebagai Penjual")
    PrimaryTabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onSurface,
        divider = {},
        modifier = modifier
    ) {
        tabs.forEachIndexed { index, label ->
            val selected = selectedTab == index
            Tab(
                selected = selected,
                onClick = { onTabSelected(index) },
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
}

@Composable
private fun OrderRow(
    order: Order,
    isBuyerView: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = order.listingTitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            StatusChip(status = order.status)
        }

        Spacer(Modifier.height(6.dp))

        val counterpartyLine = if (isBuyerView) {
            "Penjual: ${order.sellerName}"
        } else {
            "Pembeli: ${order.buyerName}"
        }
        Text(
            text = counterpartyLine,
            style = MaterialTheme.typography.bodySmall,
            color = berimaColors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = formatRupiah(order.price),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatRelativeTime(order.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = berimaColors.textSecondary
            )
        }
    }
}

@Composable
private fun EmptyOrders(
    isBuyerView: Boolean,
    modifier: Modifier = Modifier
) {
    val title = "Belum ada pesanan"
    val body = if (isBuyerView) {
        "Mulai pesan layanan dari teman kampusmu"
    } else {
        "Listing kamu belum ada yang dipesan"
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp${formatter.format(amount)}"
}

private fun formatRelativeTime(timestamp: Timestamp): String {
    val now = System.currentTimeMillis()
    val then = timestamp.toDate().time
    val diffMs = now - then
    if (diffMs < 0L) return "Baru saja"

    val seconds = diffMs / 1000L
    val minutes = seconds / 60L
    val hours = minutes / 60L
    val days = hours / 24L

    return when {
        minutes < 1L -> "Baru saja"
        minutes < 60L -> "${minutes}m lalu"
        hours < 24L -> "${hours}j lalu"
        days < 7L -> "${days}h lalu"
        else -> SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            .format(Date(then))
    }
}

// region Previews

private fun previewTimestamp(offsetMillis: Long): Timestamp {
    val target = System.currentTimeMillis() - offsetMillis
    return Timestamp(target / 1000L, ((target % 1000L) * 1_000_000L).toInt())
}

private val sampleOrders: List<Order> = listOf(
    Order(
        orderId = "o1",
        listingTitle = "Desain Logo Profesional untuk Brand Kamu",
        buyerId = "b1",
        buyerName = "Rina Anggraini",
        sellerId = "s1",
        sellerName = "Andi Pratama",
        price = 75000L,
        status = OrderStatus.PENDING,
        createdAt = previewTimestamp(2 * 60 * 1000L)
    ),
    Order(
        orderId = "o2",
        listingTitle = "Analisis Data Statistik Tugas Akhir SPSS + Interpretasi",
        buyerId = "b1",
        buyerName = "Rina Anggraini",
        sellerId = "s2",
        sellerName = "Siti Rahayu",
        price = 120000L,
        status = OrderStatus.IN_PROGRESS,
        createdAt = previewTimestamp(3 * 60 * 60 * 1000L)
    ),
    Order(
        orderId = "o3",
        listingTitle = "Edit Skripsi Sesuai Format Kampus",
        buyerId = "b1",
        buyerName = "Rina Anggraini",
        sellerId = "s3",
        sellerName = "Bayu Wicaksono",
        price = 50000L,
        status = OrderStatus.PAID,
        createdAt = previewTimestamp(2 * 24 * 60 * 60 * 1000L)
    ),
    Order(
        orderId = "o4",
        listingTitle = "Desain Poster Acara Himpunan",
        buyerId = "b1",
        buyerName = "Rina Anggraini",
        sellerId = "s4",
        sellerName = "Maya Lestari",
        price = 35000L,
        status = OrderStatus.CANCELLED,
        createdAt = previewTimestamp(10L * 24 * 60 * 60 * 1000L)
    )
)

@Preview(name = "OrdersScreen · empty (buyer)", showBackground = true, showSystemUi = true)
@Composable
private fun OrdersScreenEmptyPreview() {
    BerimaTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                OrdersTabRow(selectedTab = 0, onTabSelected = {})
                EmptyOrders(
                    isBuyerView = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview(name = "OrdersScreen · populated (buyer)", showBackground = true, showSystemUi = true)
@Composable
private fun OrdersScreenPopulatedPreview() {
    BerimaTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                OrdersTabRow(selectedTab = 0, onTabSelected = {})
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sampleOrders, key = { it.orderId }) { order ->
                        OrderRow(
                            order = order,
                            isBuyerView = true,
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// endregion
