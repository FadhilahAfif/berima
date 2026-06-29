package upnvj.berima.v1.ui.order

import upnvj.berima.v1.ui.common.formatRupiah
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
import androidx.compose.material3.AlertDialog
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
import upnvj.berima.v1.data.model.Validation
import upnvj.berima.v1.ui.common.AppStrings
import upnvj.berima.v1.ui.common.InitialAvatar
import upnvj.berima.v1.ui.common.StatusChip
import upnvj.berima.v1.ui.order.components.ChatBubble
import upnvj.berima.v1.ui.order.components.OrderActions
import upnvj.berima.v1.ui.order.components.OrderStatusTimeline
import upnvj.berima.v1.ui.theme.LocalBerimaColors

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
                        text = AppStrings.ORDER_DETAIL_TITLE,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
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
                        text = AppStrings.ORDER_DETAIL_NOT_FOUND,
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
    var showRevisionDialog by remember { mutableStateOf(false) }
    var revisionNote by remember { mutableStateOf("") }

    if (showRevisionDialog) {
        RevisionRequestDialog(
            note = revisionNote,
            onNoteChange = { if (it.length <= Validation.MAX_ORDER_NOTE_LENGTH) revisionNote = it },
            onDismiss = {
                showRevisionDialog = false
                revisionNote = ""
            },
            onSubmit = {
                onAction(OrderAction.RequestRevision(revisionNote))
                showRevisionDialog = false
                revisionNote = ""
            }
        )
    }

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

        Spacer(Modifier.height(24.dp))

        StatusBlock(
            status = order.status,
            isBuyer = isBuyer,
            modifier = Modifier.fillMaxWidth()
        )

        if (!order.note.isNullOrBlank() || !order.requirementFileUrl.isNullOrBlank()) {
            Spacer(Modifier.height(20.dp))
            RequirementCard(
                note = order.note,
                fileName = order.requirementFileName,
                onFileClick = order.requirementFileUrl?.let { url -> { onAttachmentClick(url) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!order.revisionNote.isNullOrBlank()) {
            Spacer(Modifier.height(16.dp))
            NoteCard(
                label = AppStrings.ORDER_DETAIL_REVISION_LABEL,
                note = order.revisionNote,
                modifier = Modifier.fillMaxWidth()
            )
        }

        order.attachmentUrl?.let { url ->
            Spacer(Modifier.height(16.dp))
            AttachmentRow(
                title = AppStrings.ORDER_DETAIL_RESULT_LABEL,
                fileName = order.resultFileName,
                onClick = { onAttachmentClick(url) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(20.dp))

        CounterpartyRow(
            name = counterpartyName,
            isBuyer = isBuyer,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        OrderActions(
            status = order.status,
            isBuyer = isBuyer,
            hasReview = order.hasReview,
            canRequestRevision = order.revisionCount < 1L,
            actionInFlight = actionInFlight,
            onAction = onAction,
            onPickFile = onPickFile,
            onRequestRevision = { showRevisionDialog = true },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        HorizontalDivider(thickness = 1.dp, color = berimaColors.borderSubtle)

        Spacer(Modifier.height(20.dp))

        Text(
            text = "${AppStrings.ORDER_CHAT_TITLE_PREFIX} $counterpartyName",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(12.dp))

        ChatMessageList(
            messages = messages,
            currentUserId = currentUserId,
            counterpartyName = counterpartyName,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 480.dp)
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
    val cardShape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(berimaColors.containerGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_berima_mark),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = formatRupiah(price),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.width(8.dp))

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = berimaColors.textSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}

/**
 * Open (card-less) status block on cream: an overline label, the status chip
 * paired with a contextual sentence describing what is happening and the next
 * step, then the 5-dot timeline. Sits against the white summary card above for
 * surface contrast without nesting cards.
 */
@Composable
private fun StatusBlock(
    status: String,
    isBuyer: Boolean,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    Column(modifier = modifier) {
        Text(
            text = AppStrings.ORDER_DETAIL_STATUS_LABEL,
            style = MaterialTheme.typography.labelSmall,
            color = berimaColors.textSecondary
        )
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusChip(status = status)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = statusDescription(status, isBuyer),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(18.dp))
        OrderStatusTimeline(currentStatus = status)
    }
}

private fun statusDescription(status: String, isBuyer: Boolean): String = when (status) {
    OrderStatus.PENDING ->
        if (isBuyer) AppStrings.ORDER_STATUS_PENDING_BUYER else AppStrings.ORDER_STATUS_PENDING_SELLER
    OrderStatus.IN_PROGRESS ->
        if (isBuyer) AppStrings.ORDER_STATUS_IN_PROGRESS_BUYER else AppStrings.ORDER_STATUS_IN_PROGRESS_SELLER
    OrderStatus.DELIVERED ->
        if (isBuyer) AppStrings.ORDER_STATUS_DELIVERED_BUYER else AppStrings.ORDER_STATUS_DELIVERED_SELLER
    OrderStatus.REVISION_REQUESTED ->
        if (isBuyer) AppStrings.ORDER_STATUS_REVISION_BUYER else AppStrings.ORDER_STATUS_REVISION_SELLER
    OrderStatus.COMPLETED ->
        if (isBuyer) AppStrings.ORDER_STATUS_COMPLETED_BUYER else AppStrings.ORDER_STATUS_COMPLETED_SELLER
    OrderStatus.PAID ->
        if (isBuyer) AppStrings.ORDER_STATUS_PAID_BUYER else AppStrings.ORDER_STATUS_PAID_SELLER
    OrderStatus.CANCELLED -> AppStrings.ORDER_STATUS_CANCELLED
    OrderStatus.REJECTED -> AppStrings.ORDER_STATUS_REJECTED
    else -> ""
}

/**
 * Display-only person row for the counterparty: a circular monogram avatar, an
 * overline role label, and the name. Replaces the bare "Penjual: X" text line.
 */
@Composable
private fun CounterpartyRow(
    name: String,
    isBuyer: Boolean,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val roleLabel = if (isBuyer) {
        AppStrings.ORDER_DETAIL_COUNTERPARTY_SELLER
    } else {
        AppStrings.ORDER_DETAIL_COUNTERPARTY_BUYER
    }
    val cardShape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InitialAvatar(name = name, size = 44.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = roleLabel,
                style = MaterialTheme.typography.labelSmall,
                color = berimaColors.textSecondary
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RequirementCard(
    note: String?,
    fileName: String?,
    onFileClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .padding(14.dp)
    ) {
        Text(
            text = AppStrings.ORDER_DETAIL_REQUIREMENT_LABEL,
            style = MaterialTheme.typography.labelSmall,
            color = berimaColors.textSecondary
        )
        if (!note.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = note,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (onFileClick != null) {
            Spacer(Modifier.height(12.dp))
            FileInlineRow(
                title = AppStrings.ORDER_DETAIL_REQUIREMENT_FILE_TITLE,
                fileName = fileName,
                onClick = onFileClick
            )
        }
    }
}

@Composable
private fun NoteCard(
    label: String,
    note: String,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .padding(14.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = berimaColors.textSecondary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = note,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AttachmentRow(
    title: String,
    fileName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val cardShape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, berimaColors.borderSubtle, cardShape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
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
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = fileName ?: AppStrings.ORDER_DETAIL_FILE_ACTION,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = berimaColors.textSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun FileInlineRow(
    title: String,
    fileName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_berima_mark),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = fileName ?: AppStrings.ORDER_DETAIL_FILE_ACTION,
                style = MaterialTheme.typography.bodySmall,
                color = berimaColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RevisionRequestDialog(
    note: String,
    onNoteChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    val berimaColors = LocalBerimaColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.ORDER_REVISION_DIALOG_TITLE) },
        text = {
            Column {
                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    label = { Text(AppStrings.ORDER_REVISION_DIALOG_LABEL) },
                    placeholder = { Text(AppStrings.ORDER_REVISION_DIALOG_PLACEHOLDER) },
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = berimaColors.borderInput,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = berimaColors.textSecondary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${note.length}/${Validation.MAX_ORDER_NOTE_LENGTH}",
                    style = MaterialTheme.typography.labelSmall,
                    color = berimaColors.textSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSubmit,
                enabled = note.isNotBlank()
            ) {
                Text(AppStrings.ORDER_REVISION_DIALOG_SUBMIT)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.ORDER_REVISION_DIALOG_CANCEL)
            }
        }
    )
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
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(berimaColors.containerGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_mail),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = AppStrings.ORDER_CHAT_EMPTY_TITLE,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${AppStrings.ORDER_CHAT_EMPTY_BODY_PREFIX} $counterpartyName",
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
                    text = AppStrings.ORDER_CHAT_INPUT_PLACEHOLDER,
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
                            text = AppStrings.ORDER_DETAIL_TITLE,
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
