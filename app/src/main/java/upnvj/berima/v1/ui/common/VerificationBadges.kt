package upnvj.berima.v1.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import upnvj.berima.v1.R
import upnvj.berima.v1.ui.theme.LocalBerimaColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VerificationBadgeRow(
    isIdentityVerified: Boolean,
    skillBadges: List<String>,
    modifier: Modifier = Modifier,
    relevantSkillCategory: String? = null
) {
    val skillsToShow = if (relevantSkillCategory == null) {
        skillBadges
    } else {
        skillBadges.filter { it == relevantSkillCategory }
    }

    if (!isIdentityVerified && skillsToShow.isEmpty()) return

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isIdentityVerified) {
            IdentityBadge()
        }
        skillsToShow.forEach { category ->
            SkillBadge(category = category)
        }
    }
}

@Composable
fun ListingSkillBadge(
    category: String,
    verifiedSkillBadges: List<String>,
    modifier: Modifier = Modifier
) {
    if (category !in verifiedSkillBadges) return
    SkillBadge(category = category, modifier = modifier, compact = true)
}

@Composable
private fun IdentityBadge(modifier: Modifier = Modifier) {
    BadgePill(
        label = AppStrings.BADGE_IDENTITY,
        modifier = modifier
    )
}

@Composable
private fun SkillBadge(
    category: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val label = if (compact) {
        AppStrings.BADGE_SKILL_COMPACT.format(categoryLabel(category))
    } else {
        AppStrings.BADGE_SKILL.format(categoryLabel(category))
    }
    BadgePill(label = label, modifier = modifier)
}

@Composable
private fun BadgePill(
    label: String,
    modifier: Modifier = Modifier
) {
    val berimaColors = LocalBerimaColors.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(berimaColors.containerGreen)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f), RoundedCornerShape(9999.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(13.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
