package upnvj.berima.v1.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Public seller work sample in `portfolioItems/{portfolioItemId}`.
 */
data class PortfolioItem(
    @DocumentId val portfolioItemId: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = Category.ACADEMIC,
    val externalLink: String? = null,
    val imageUrl: String? = null,
    val imageStoragePath: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
