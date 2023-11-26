package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.ui.utils.formatAsAverage
import ca.josephroque.bowlingcompanion.core.model.ui.utils.seriesDate
import kotlinx.datetime.LocalDate

@Composable
fun LeagueRow(
	name: String,
	modifier: Modifier = Modifier,
	onClick: (() -> Unit)? = null,
	recurrence: LeagueRecurrence? = null,
	lastSeriesDate: LocalDate? = null,
	average: Double? = null,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxWidth()
			.then(if (onClick != null)
				Modifier
					.clickable(onClick = onClick)
					.padding(16.dp)
			else Modifier),
	) {
		recurrence?.let {
			Icon(
				it.listIcon(),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.size(24.dp),
			)
		}

		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = name,
				style = MaterialTheme.typography.titleMedium,
			)

			if (lastSeriesDate != null && recurrence != null) {
				Text(
					text = lastSeriesDate.seriesDate(recurrence = recurrence),
					style = MaterialTheme.typography.bodyMedium,
					fontStyle = FontStyle.Italic,
				)
			}
		}

		Text(
			text = average.formatAsAverage(),
			style = MaterialTheme.typography.bodyLarge,
			maxLines = 1,
		)
	}
}

@Composable
private fun LeagueRecurrence.listIcon(): Painter = when (this) {
	LeagueRecurrence.ONCE -> painterResource(R.drawable.ic_ticket)
	LeagueRecurrence.REPEATING -> painterResource(R.drawable.ic_event_repeat)
}

@Preview
@Composable
private fun LeagueItemPreview() {
	Surface {
		LeagueRow(
			name = "Majors, 23/24",
			recurrence = LeagueRecurrence.REPEATING,
			average = 234.0,
			lastSeriesDate = LocalDate.parse("2023-10-01"),
			onClick = {},
		)
	}
}