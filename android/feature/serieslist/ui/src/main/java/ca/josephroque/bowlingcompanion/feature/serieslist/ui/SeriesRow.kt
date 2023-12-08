package ca.josephroque.bowlingcompanion.feature.serieslist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
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
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
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

@Composable
fun SeriesRow(
	series: SeriesListChartItem,
	itemSize: SeriesItemSize,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	when (itemSize) {
		SeriesItemSize.DEFAULT -> DefaultSeriesRow(series, onClick, modifier)
		SeriesItemSize.COMPACT -> CompactSeriesRow(series, onClick, modifier)
	}
}

@Composable
private fun CompactSeriesRow(
	series: SeriesListChartItem,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Header(
		date = series.date,
		total = series.total,
		itemSize = SeriesItemSize.COMPACT,
		modifier = modifier
			.clickable(onClick = onClick)
			.padding(16.dp),
	)
}

@Composable
private fun DefaultSeriesRow(
	series: SeriesListChartItem,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		contentAlignment = Alignment.BottomStart,
		modifier = modifier
			.clickable(onClick = onClick)
			.padding(bottom = 8.dp),
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
			Header(
				date = series.date,
				total = series.total,
				itemSize = SeriesItemSize.DEFAULT,
			)

			ScoreSummary(
				numberOfGames = series.numberOfGames,
				seriesLow = series.lowestScore,
				seriesHigh = series.highestScore,
				scores = series.scores,
			)
		}
	}
}

@Composable
private fun Header(
	date: LocalDate,
	total: Int,
	itemSize: SeriesItemSize,
	modifier: Modifier = Modifier,
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

			Text(
				text = date.simpleFormat(),
				style = MaterialTheme.typography.titleMedium,
			)
		}

		if (total > 0) {
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
					// FIXME: Decide how the yRange is calculated for iOS and Android
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
				itemSize = SeriesItemSize.DEFAULT,
				onClick = {},
			)

			Divider(modifier = Modifier.padding(start = 16.dp))

			SeriesRow(
				series = SeriesListChartItem(
					id = UUID.randomUUID(),
					date = LocalDate.parse("2023-10-01"),
					total = 880,
					preBowl = SeriesPreBowl.REGULAR,
					numberOfGames = 1,
					lowestScore = 220,
					highestScore = 220,
					scores = ChartEntryModelProducer(
						listOf(entryOf(0, 220))
					),
				),
				itemSize = SeriesItemSize.COMPACT,
				onClick = {},
			)
		}
	}
}