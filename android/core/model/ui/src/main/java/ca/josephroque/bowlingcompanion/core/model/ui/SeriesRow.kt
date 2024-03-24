package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import kotlinx.datetime.LocalDate

@Composable
fun SeriesRow(
	date: LocalDate,
	modifier: Modifier = Modifier,
	preBowledForDate: LocalDate? = null,
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
				painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_event),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.size(16.dp),
			)

			Column {
				Text(
					text = (preBowledForDate ?: date).simpleFormat(),
					style = MaterialTheme.typography.titleMedium,
				)

				if (preBowledForDate != null) {
					Text(
						text = stringResource(R.string.series_property_originally_bowled, date.simpleFormat()),
						style = MaterialTheme.typography.bodySmall,
					)
				}
			}
		}

		if (total != null && total > 0) {
			Column(horizontalAlignment = Alignment.End) {
				Text(
					text = total.toString(),
					style = when (itemSize) {
						SeriesItemSize.DEFAULT -> MaterialTheme.typography.headlineMedium
						SeriesItemSize.COMPACT -> MaterialTheme.typography.headlineSmall
					},
					fontWeight = FontWeight.Black,
					fontStyle = FontStyle.Italic,
				)

				when (itemSize) {
					SeriesItemSize.DEFAULT -> Text(
						text = stringResource(R.string.series_property_total),
						style = MaterialTheme.typography.bodySmall,
						fontStyle = FontStyle.Italic,
					)

					SeriesItemSize.COMPACT -> Unit
				}
			}
		}
	}
}

@Composable
@Preview
fun SeriesRowPreview() {
	Column {
		SeriesRow(LocalDate(2022, 1, 1), total = 300)
		SeriesRow(
			date = LocalDate(2022, 1, 1),
			preBowledForDate = LocalDate(2022, 1, 1),
			total = 300,
		)
		SeriesRow(LocalDate(2022, 1, 1), total = 300, itemSize = SeriesItemSize.DEFAULT)
		SeriesRow(
			date = LocalDate(2022, 1, 1),
			preBowledForDate = LocalDate(2022, 1, 1),
			total = 300,
			itemSize = SeriesItemSize.DEFAULT,
		)
	}
}
