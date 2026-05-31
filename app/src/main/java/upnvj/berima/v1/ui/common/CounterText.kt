package upnvj.berima.v1.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@Composable
fun CounterText(current: Int, max: Int) {
    val berimaColors = LocalBerimaColors.current
    Text(
        text = "$current/$max",
        style = MaterialTheme.typography.bodyMedium,
        color = if (current >= max) MaterialTheme.colorScheme.error else berimaColors.textSecondary,
        modifier = Modifier.fillMaxWidth()
    )
}
