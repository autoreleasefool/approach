package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.R
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import kotlinx.datetime.LocalDate

@Composable
fun SeriesRow(
	date: LocalDate,
	modifier: Modifier = Modifier,
	total: Int? = null,
	itemSize: SeriesItemSize = SeriesItemSize.COMPACT,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalAlignment = when (itemSize) {
			SeriesItemSize.DEFAULT -> Alignment.Top
			SeriesItemSize.COMPACT -> Alignment.CenterVertically
		},
		modifier = modifier.fillMaxWidth(),
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.weight(1f),
		) {
			Icon(
				painterResource(R.drawable.ic_event),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.size(16.dp),
			)

			Text(
				text = date.simpleFormat(),
				style = MaterialTheme.typography.titleMedium,
			)
		}

		if (total != null && total > 0) {
			Text(
				text = total.toString(),
				style = when (itemSize) {
					SeriesItemSize.DEFAULT -> MaterialTheme.typography.headlineMedium
					SeriesItemSize.COMPACT -> MaterialTheme.typography.headlineSmall
				},
				fontWeight = FontWeight.Black,
				fontStyle = FontStyle.Italic,
			)
		}
	}
}