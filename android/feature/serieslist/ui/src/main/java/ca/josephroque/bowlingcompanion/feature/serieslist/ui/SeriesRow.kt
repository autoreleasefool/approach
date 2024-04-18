package ca.josephroque.bowlingcompanion.feature.serieslist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.datetime.LocalDate

data class ScoreData(
	val numberOfGames: Int,
	val seriesLow: Int,
	val seriesHigh: Int,
	val model: ChartEntryModelProducer,
)

@Composable
fun SeriesRow(
	date: LocalDate,
	total: Int,
	itemSize: SeriesItemSize,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	preBowledForDate: LocalDate? = null,
	scores: ScoreData? = null,
) {
	if (scores == null) {
		CompactSeriesRow(date, preBowledForDate, total, onClick, modifier)
		return
	}

	when (itemSize) {
		SeriesItemSize.DEFAULT -> DefaultSeriesRow(
			date = date,
			preBowledForDate = preBowledForDate,
			total = total,
			scores = scores,
			onClick = onClick,
			modifier = modifier,
		)
		SeriesItemSize.COMPACT -> CompactSeriesRow(date, preBowledForDate, total, onClick, modifier)
	}
}

@Composable
private fun CompactSeriesRow(
	date: LocalDate,
	preBowledForDate: LocalDate?,
	total: Int,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	ca.josephroque.bowlingcompanion.core.model.ui.SeriesRow(
		date = date,
		preBowledForDate = preBowledForDate,
		total = total,
		itemSize = SeriesItemSize.COMPACT,
		modifier = modifier
			.clickable(onClick = onClick)
			.padding(16.dp),
	)
}

@Composable
private fun DefaultSeriesRow(
	date: LocalDate,
	preBowledForDate: LocalDate?,
	total: Int,
	scores: ScoreData,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		contentAlignment = Alignment.BottomStart,
		modifier = modifier
			.clickable(onClick = onClick)
			.padding(bottom = 8.dp),
	) {
		ScoreChart(
			scores = scores.model,
			seriesLow = scores.seriesLow,
			seriesHigh = scores.seriesHigh,
		)

		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier.padding(16.dp),
		) {
			ca.josephroque.bowlingcompanion.core.model.ui.SeriesRow(
				date = date,
				preBowledForDate = preBowledForDate,
				total = total,
				itemSize = SeriesItemSize.DEFAULT,
			)

			ScoreSummary(
				numberOfGames = scores.numberOfGames,
				seriesLow = scores.seriesLow,
				seriesHigh = scores.seriesHigh,
				scores = scores.model,
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
) {
	Column(
		modifier = Modifier
			.then(
				if (scores == null) {
					Modifier
				} else {
					Modifier
						.background(
							colorResource(
								ca.josephroque.bowlingcompanion.core.designsystem.R.color.yellow_300,
							).copy(alpha = 0.5F),
							MaterialTheme.shapes.medium,
						)
				},
			)
			.padding(vertical = 2.dp, horizontal = 4.dp),
	) {
		Text(
			text = pluralStringResource(R.plurals.games_count, numberOfGames, numberOfGames),
			style = MaterialTheme.typography.bodyMedium,
		)

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

@Composable
private fun ScoreChart(seriesLow: Int, seriesHigh: Int, scores: ChartEntryModelProducer) {
	ProvideChartStyle(
		chartStyle = rememberChartStyle(
			lineChartColors = listOf(
				Pair(
					colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_300),
					colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_300),
				),
			),
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
				date = LocalDate.parse("2023-09-24"),
				preBowledForDate = LocalDate.parse("2023-09-24"),
				total = 880,
				scores = ScoreData(
					numberOfGames = 4,
					seriesLow = 215,
					seriesHigh = 230,
					model = ChartEntryModelProducer(
						listOf(
							entryOf(0, 220),
							entryOf(1, 230),
							entryOf(2, 215),
							entryOf(3, 225),
						),
					),
				),
				itemSize = SeriesItemSize.DEFAULT,
				onClick = {},
			)

			HorizontalDivider(modifier = Modifier.padding(start = 16.dp))

			SeriesRow(
				date = LocalDate.parse("2023-10-01"),
				total = 880,
				scores = null,
				itemSize = SeriesItemSize.DEFAULT,
				onClick = {},
			)

			HorizontalDivider(modifier = Modifier.padding(start = 16.dp))

			SeriesRow(
				date = LocalDate.parse("2023-10-01"),
				total = 880,
				scores = null,
				itemSize = SeriesItemSize.COMPACT,
				onClick = {},
			)
		}
	}
}
