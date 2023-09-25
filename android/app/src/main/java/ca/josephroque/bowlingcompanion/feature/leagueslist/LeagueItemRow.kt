package ca.josephroque.bowlingcompanion.feature.leagueslist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.utils.formatAsAverage
import ca.josephroque.bowlingcompanion.utils.seriesDate
import kotlinx.datetime.Instant
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueItemRow(
	league: LeagueListItem,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(
		onClick = onClick,
		colors = CardDefaults.cardColors(
			containerColor = colorResource(R.color.purple_100),
		),
		modifier = modifier,
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(8.dp)
		) {
			Icon(
				league.recurrence.listIcon(),
				contentDescription = null,
			)
			Spacer(Modifier.width(8.dp))
			Column {
				Text(
					text = league.name,
					fontSize = 18.sp,
					fontWeight = FontWeight.Bold,
				)
				Text(
					text = league.lastSeriesDate.seriesDate(recurrence = league.recurrence),
					fontSize = 12.sp,
					fontStyle = FontStyle.Italic,
				)
			}
			Spacer(
				Modifier
					.defaultMinSize(minWidth = 8.dp)
					.weight(1f)
			)
			Text(league.average.formatAsAverage())
		}
	}
}

@Composable
fun LeagueRecurrence.listIcon(): Painter = when (this) {
	LeagueRecurrence.ONCE -> painterResource(R.drawable.ic_ticket)
	LeagueRecurrence.REPEATING -> painterResource(R.drawable.ic_event_repeat)
}

@Preview
@Composable
fun LeagueItemPreview() {
	Surface {
		LeagueItemRow(
			league = LeagueListItem(
				id = UUID.randomUUID(),
				name = "Majors, 23/24",
				recurrence = LeagueRecurrence.REPEATING,
				average = 234.0,
				lastSeriesDate = Instant.fromEpochSeconds(1695530266L),
			),
			onClick = {},
		)
	}
}