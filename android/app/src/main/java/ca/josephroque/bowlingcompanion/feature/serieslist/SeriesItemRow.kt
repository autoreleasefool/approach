package ca.josephroque.bowlingcompanion.feature.serieslist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.utils.format
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import kotlin.math.roundToInt

data class SeriesChartable(
	val id: UUID,
	val date: Instant,
	val preBowl: SeriesPreBowl,
	val total: Int,
	val numberOfGames: Int,
	val scores: ChartEntryModel?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesItemRow(
	series: SeriesChartable,
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
		Box(
			contentAlignment = Alignment.BottomStart,
		) {
			if (series.scores != null) {
				ScoreChart(series.scores)
			}

			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier
					.padding(8.dp)
			) {
				Header(series.date)

				Row {
					ScoreSummary(
						numberOfGames = series.numberOfGames,
						scores = series.scores,
						modifier = Modifier.alignBy(LastBaseline),
					)

					Spacer(modifier = Modifier.weight(1F))

					if (series.total > 0) {
						Text(
							text = series.total.toString(),
							style = MaterialTheme.typography.headlineMedium,
							fontStyle = FontStyle.Italic,
							modifier = Modifier.alignBy(LastBaseline),
						)
					}
				}
			}
		}
	}
}

@Composable
private fun Header(date: Instant) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Icon(
			painterResource(R.drawable.ic_calendar),
			contentDescription = null,
			modifier = Modifier.size(16.dp),
		)

		Text(
			text = date.toLocalDateTime(TimeZone.currentSystemDefault())
				.format("MMMM d, yyyy"),
			style = MaterialTheme.typography.titleMedium,
		)
	}
}

@Composable
private fun ScoreSummary(
	numberOfGames: Int,
	scores: ChartEntryModel?,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.then(
				if (scores == null) {
					Modifier
				} else {
					Modifier
						.background(
							colorResource(R.color.purple_200).copy(alpha = 0.5F),
							RoundedCornerShape(4.dp)
						)
				}
			)
			.padding(vertical = 2.dp, horizontal = 4.dp)
	) {
		Text(
			text = pluralStringResource(R.plurals.games_count, numberOfGames, numberOfGames),
			style = MaterialTheme.typography.bodyMedium,
		)

		if (scores != null) {
			Text(
				text = stringResource(
					R.string.series_list_score_range,
					scores.minY.roundToInt(),
					scores.maxY.roundToInt()
				),
				style = MaterialTheme.typography.bodyMedium,
				fontStyle = FontStyle.Italic,
			)
		}
	}
}

@Composable
private fun ScoreChart(scores: ChartEntryModel) {
	ProvideChartStyle(
		chartStyle = rememberChartStyle(chartColors = listOf(colorResource(R.color.yellow_300))),
	) {
		Chart(
			chart = lineChart(
				axisValuesOverrider = AxisValuesOverrider.fixed(
					// TODO: Decide how the yRange is calculated for iOS and Android
					// iOS currently sets the yRange to 0..450
					// Android sets the range to minScore..maxScore
					// Results in iOS having less dramatic charts, but are relative to one another
					// Android has greater variation in charts, but cannot be compared to one another
					minY = scores.minY - 5,
					maxY = scores.maxY + 5,
				),
			),
			model = scores,
			horizontalLayout = HorizontalLayout.FullWidth(),
			modifier = Modifier
				.fillMaxWidth()
				.height(48.dp),
		)
	}
}

@Preview
@Composable
fun SeriesItemPreview() {
	Surface {
		Column {
			SeriesItemRow(
				series = SeriesChartable(
					id = UUID.randomUUID(),
					date = Clock.System.now(),
					total = 880,
					preBowl = SeriesPreBowl.REGULAR,
					numberOfGames = 4,
					scores = entryModelOf(220, 230, 215, 225),
				),
				onClick = {},
				modifier = Modifier.padding(16.dp),
			)
			SeriesItemRow(
				series = SeriesChartable(
					id = UUID.randomUUID(),
					date = Clock.System.now(),
					total = 880,
					preBowl = SeriesPreBowl.REGULAR,
					numberOfGames = 1,
					scores = null,
				),
				onClick = {},
				modifier = Modifier.padding(16.dp),
			)
		}
	}
}