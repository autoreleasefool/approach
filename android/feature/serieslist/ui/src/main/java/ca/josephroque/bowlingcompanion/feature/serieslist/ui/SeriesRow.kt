package ca.josephroque.bowlingcompanion.feature.serieslist.ui

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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.datetime.LocalDate
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesRow(
	series: SeriesListChartItem,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(
		onClick = onClick,
		modifier = modifier.padding(horizontal = 16.dp),
	) {
		Box(
			contentAlignment = Alignment.BottomStart,
		) {
			if (series.scores != null) {
				ScoreChart(
					scores = series.scores,
					seriesLow = series.lowestScore,
					seriesHigh = series.highestScore,
				)
			}

			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier.padding(16.dp),
			) {
				Row {
					Header(series.date)

					Spacer(modifier = Modifier.weight(1f))

					if (series.total > 0) {
						Text(
							text = series.total.toString(),
							style = MaterialTheme.typography.headlineMedium,
							fontWeight = FontWeight.Black,
							fontStyle = FontStyle.Italic,
						)
					}
				}

				ScoreSummary(
					numberOfGames = series.numberOfGames,
					scores = series.scores,
					seriesLow = series.lowestScore,
					seriesHigh = series.highestScore,
				)
			}
		}
	}
}

@Composable
private fun Header(date: LocalDate) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Icon(
			painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_event),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.size(16.dp),
		)

		Text(
			text = date.simpleFormat(),
			style = MaterialTheme.typography.titleMedium,
		)
	}
}

@Composable
private fun ScoreSummary(
	numberOfGames: Int,
	seriesLow: Int,
	seriesHigh: Int,
	scores: ChartEntryModelProducer?,
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
							colorResource(RCoreDesign.color.yellow_300).copy(alpha = 0.5F),
							MaterialTheme.shapes.medium,
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
					seriesLow,
					seriesHigh,
				),
				style = MaterialTheme.typography.bodyMedium,
				fontStyle = FontStyle.Italic,
			)
		}
	}
}

@Composable
private fun ScoreChart(
	seriesLow: Int,
	seriesHigh: Int,
	scores: ChartEntryModelProducer,
) {
	ProvideChartStyle(
		chartStyle = rememberChartStyle(
			chartColors = listOf(colorResource(RCoreDesign.color.purple_300))
		),
	) {
		Chart(
			chart = lineChart(
				axisValuesOverrider = AxisValuesOverrider.fixed(
					// TODO: Decide how the yRange is calculated for iOS and Android
					// iOS currently sets the yRange to 0..450
					// Android sets the range to minScore..maxScore
					// Results in iOS having less dramatic charts, but are relative to one another
					// Android has greater variation in charts, but cannot be compared to one another
					minY = seriesLow.toFloat() - 5,
					maxY = seriesHigh.toFloat() + 5,
				),
			),
			chartModelProducer = scores,
			horizontalLayout = HorizontalLayout.FullWidth(),
			modifier = Modifier
				.fillMaxWidth()
				.height(48.dp),
		)
	}
}

@Preview
@Composable
private fun SeriesItemPreview() {
	Surface {
		Column {
			SeriesRow(
				series = SeriesListChartItem(
					id = UUID.randomUUID(),
					date = LocalDate.parse("2023-09-24"),
					total = 880,
					preBowl = SeriesPreBowl.REGULAR,
					numberOfGames = 4,
					lowestScore = 215,
					highestScore = 230,
					scores = ChartEntryModelProducer(listOf(
						entryOf(0, 220),
						entryOf(1, 230),
						entryOf(2, 215),
						entryOf(3, 225),
					)),
				),
				onClick = {},
				modifier = Modifier.padding(bottom = 16.dp),
			)
			SeriesRow(
				series = SeriesListChartItem(
					id = UUID.randomUUID(),
					date = LocalDate.parse("2023-10-01"),
					total = 880,
					preBowl = SeriesPreBowl.REGULAR,
					numberOfGames = 1,
					lowestScore = 220,
					highestScore = 220,
					scores = ChartEntryModelProducer(listOf(entryOf(0, 220))),
				),
				onClick = {},
			)
		}
	}
}