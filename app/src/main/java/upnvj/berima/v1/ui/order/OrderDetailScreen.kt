package upnvj.berima.v1.ui.order

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import upnvj.berima.v1.R
import upnvj.berima.v1.data.model.Message
import upnvj.berima.v1.data.model.Order
import upnvj.berima.v1.data.model.OrderStatus
import upnvj.berima.v1.ui.common.StatusChip
import upnvj.berima.v1.ui.order.components.ChatBubble
import upnvj.berima.v1.ui.order.components.OrderActions
import upnvj.berima.v1.ui.order.components.OrderStatusTimeline
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    onNavigateBack: () -> Unit,
    onListingClick: (String) -> Unit,
    onReviewClick: (String) -> Unit,
    detailViewModel: OrderDetailViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val order by detailViewModel.order.collectAsStateWithLifecycle()
    val isBuyer by detailViewModel.isBuyer.collectAsStateWithLifecycle()
    val isLoading by detailViewModel.isLoading.collectAsStateWithLifecycle()
    val actionInFlight by detailViewModel.actionInFlight.collectAsStateWithLifecycle()
    val detailError by detailViewModel.error.collectAsStateWithLifecycle()

    val messages by chatViewModel.messages.collectAsStateWithLifecycle()
    val draft by chatViewModel.draft.collectAsStateWithLifecycle()
    val isSending by chatViewModel.isSending.collectAsStateWithLifecycle()
    val chatError by chatViewModel.error.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val berimaColors = LocalBerimaColors.current
    val context = LocalContext.current

    val pickFile = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { detailViewModel.onAction(OrderAction.UploadResult(it)) }
    }

    LaunchedEffect(detailError) {
        detailError?.let {
            snackbarHostState.showSnackbar(it)
            detailViewModel.clearError()
        }
    }

    LaunchedEffect(chatError) {
        chatError?.let {
            snackbarHostState.showSnackbar(it)
            chatViewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        detailViewModel.navigateToReview.collect { orderId ->
            onReviewClick(orderId)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Pesanan",
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

            order == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pesanan tidak ditemukan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = berimaColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                val o = order!!
                OrderDetailContent(
                    order = o,
                    isBuyer = isBuyer,
                    actionInFlight = actionInFlight,
                    messages = messages,
                    currentUserId = chatViewModel.currentUserId,
                    draft = draft,
                    isSending = isSending,
                    onListingClick = { onListingClick(o.listingId) },
                    onAttachmentClick = { url ->
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            )
                        }
                    },
                    onAction = detailViewModel::onAction,
                    onPickFile = { pickFile.launch("*/*") },
                    onDraftChange = chatViewModel::onDraftChange,
                    onSend = {
                        val name = if (isBuyer) o.buyerName else o.sellerName
                        chatViewModel.send(name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }
}

@Composable
private fun OrderDetailContent(
    order: Order,
    isBuyer: Boolean,
    actionInFlight: String?,
    messages: List<Message>,
    currentUserId: String?,
    draft: String,
    isSending: Boolean,
    onListingClick: () -> Unit,
    onAttachmentClick: (String) -> Unit,
    onAction: (OrderAction) -> Unit,
    onPickFile: () -> Unit,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val counterpartyName = if (isBuyer) order.sellerName else order.buyerName

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ListingSummaryCard(
            title = order.listingTitle,
            price = order.price,
            onClick = onListingClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "STATUS",
            style = MaterialTheme.typography.labelSmall,
            color = berimaColors.textSecondary
        )
        Spacer(Modifier.height(8.dp))
        StatusChip(status = order.status)
        Spacer(Modifier.height(12.dp))
        OrderStatusTimeline(currentStatus = order.status)

        if (!order.note.isNullOrBlank()) {
            Spacer(Modifier.height(16.dp))
            NoteCard(note = order.note, modifier = Modifier.fillMaxWidth())
        }

        order.attachmentUrl?.let { url ->
            Spacer(Modifier.height(16.dp))
            AttachmentRow(
                onClick = { onAttachmentClick(url) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = if (isBuyer) {
                "Penjual: ${order.sellerName}"
            } else {
                "Pembeli: ${order.buyerName}"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(16.dp))

        OrderActions(
            status = order.status,
            isBuyer = isBuyer,
            hasReview = order.hasReview,
            actionInFlight = actionInFlight,
            onAction = onAction,
            onPickFile = onPickFile,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        HorizontalDivider(thickness = 1.dp, color = berimaColors.borderSubtle)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Chat dengan $counterpartyName",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(12.dp))

        ChatMessageList(
            messages = messages,
            currentUserId = currentUserId,
            counterpartyName = counterpartyName,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 480.dp)
        )

        Spacer(Modifier.height(8.dp))

        ChatInputRow(
            draft = draft,
            isSending = isSending,
            onDraftChange = onDraftChange,
            onSend = onSend,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ListingSummaryCard(
    title: String,
    price: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_berima_mark),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatRupiah(price),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun NoteCard(
    note: String,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .padding(12.dp)
    ) {
        Text(
            text = "Catatan dari pembeli",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = note,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AttachmentRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(berimaColors.containerGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_berima_mark),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hasil pekerjaan",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Buka file",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ChatMessageList(
    messages: List<Message>,
    currentUserId: String?,
    counterpartyName: String,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    if (messages.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Mulai percakapan dengan $counterpartyName",
                style = MaterialTheme.typography.bodyMedium,
                color = berimaColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(messages, key = { it.messageId }) { message ->
            ChatBubble(
                message = message,
                isMine = message.senderId == currentUserId
            )
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
}

@Composable
private fun ChatInputRow(
    draft: String,
    isSending: Boolean,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val canSend = draft.isNotBlank() && !isSending

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = draft,
            onValueChange = onDraftChange,
            label = {
                Text(
                    text = "Tulis pesan...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            singleLine = false,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = berimaColors.borderInput,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = berimaColors.textSecondary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    if (canSend) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    }
                )
                .clickable(enabled = canSend, onClick = onSend),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\u27A4",
                style = MaterialTheme.typography.titleMedium,
                color = if (canSend) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    berimaColors.textSecondary
                }
            )
        }
    }
}

private fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp${formatter.format(amount)}"
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview(
    name = "OrderDetailScreen · in_progress",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun OrderDetailScreenPreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        val berimaColors = LocalBerimaColors.current
        val sampleOrder = Order(
            orderId = "o1",
            listingId = "l1",
            listingTitle = "Desain Logo Profesional untuk Brand Kamu",
            buyerId = "u1",
            buyerName = "Budi Santoso",
            sellerId = "u2",
            sellerName = "Andi Pratama",
            price = 75000L,
            note = "Tolong gunakan warna biru tua dengan aksen emas.",
            status = OrderStatus.IN_PROGRESS
        )
        val sampleMessages = listOf(
            Message(
                messageId = "m1",
                senderId = "u2",
                senderName = "Andi Pratama",
                text = "Halo, brief-nya bisa diperjelas?"
            ),
            Message(
                messageId = "m2",
                senderId = "u1",
                senderName = "Budi Santoso",
                text = "Siap, saya kirim referensi sebentar lagi."
            )
        )

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Detail Pesanan",
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
            OrderDetailContent(
                order = sampleOrder,
                isBuyer = true,
                actionInFlight = null,
                messages = sampleMessages,
                currentUserId = "u1",
                draft = "",
                isSending = false,
                onListingClick = {},
                onAttachmentClick = {},
                onAction = {},
                onPickFile = {},
                onDraftChange = {},
                onSend = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}
