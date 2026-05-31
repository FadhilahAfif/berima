package upnvj.berima.v1.ui.common

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp${formatter.format(amount)}"
}

/**
 * Formats a raw digit string (as typed into a price field) into the canonical
 * `Rp10.000` preview. Returns an empty string for blank/zero input so callers can
 * hide the preview until a real value is entered. Non-digits are ignored defensively.
 */
fun formatRupiahInput(raw: String): String {
    val digits = raw.filter { it.isDigit() }.trimStart('0')
    if (digits.isBlank()) return ""
    val amount = digits.toLongOrNull() ?: return ""
    return formatRupiah(amount)
}
