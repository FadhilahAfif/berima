package upnvj.berima.v1.ui.order.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import upnvj.berima.v1.data.model.Message
import upnvj.berima.v1.ui.theme.LocalBerimaColors
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * One chat message. Right-aligned and tinted with the brand primary when
 * the current user is the sender; left-aligned with a subtle outlined
 * surface otherwise.
 */
@Composable
fun ChatBubble(
    message: Message,
    isMine: Boolean,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .then(
                    if (isMine) {
                        Modifier.background(MaterialTheme.colorScheme.primary)
                    } else {
                        Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, berimaColors.borderSubtle, shape)
                    }
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isMine) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = formatTimestamp(message.createdAt.toDate().time),
                style = MaterialTheme.typography.labelSmall,
                color = if (isMine) {
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                } else {
                    berimaColors.textSecondary
                }
            )
        }
    }
}

private val timeFormatter: SimpleDateFormat
    get() = SimpleDateFormat("HH:mm", Locale("id", "ID"))

private fun formatTimestamp(epochMillis: Long): String =
    timeFormatter.format(java.util.Date(epochMillis))

@androidx.compose.ui.tooling.preview.Preview(name = "ChatBubble · pair", showBackground = true, backgroundColor = 0xFFF2EFE9)
@Composable
private fun ChatBubblePreview() {
    upnvj.berima.v1.ui.theme.BerimaTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ChatBubble(
                message = Message(
                    messageId = "m1",
                    senderId = "u2",
                    senderName = "Andi",
                    text = "Halo, brief-nya bisa dikirim?"
                ),
                isMine = false
            )
            Spacer(Modifier.height(8.dp))
            ChatBubble(
                message = Message(
                    messageId = "m2",
                    senderId = "u1",
                    senderName = "Budi",
                    text = "Siap, saya kirim sebentar lagi ya"
                ),
                isMine = true
            )
        }
    }
}
