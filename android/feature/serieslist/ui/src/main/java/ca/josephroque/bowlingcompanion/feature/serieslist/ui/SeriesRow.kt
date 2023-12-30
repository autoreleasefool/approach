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
	scores: ScoreData? = null,
) {
	if (scores == null) {
		CompactSeriesRow(date, total, onClick, modifier)
		return
	}

	when (itemSize) {
		SeriesItemSize.DEFAULT -> DefaultSeriesRow(date, total, scores, onClick, modifier)
		SeriesItemSize.COMPACT -> CompactSeriesRow(date, total, onClick, modifier)
	}
}

@Composable
private fun CompactSeriesRow(
	date: LocalDate,
	total: Int,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	ca.josephroque.bowlingcompanion.core.model.ui.SeriesRow(
		date = date,
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
				date = LocalDate.parse("2023-09-24"),
				total = 880,
				scores = ScoreData(
					numberOfGames = 4,
					seriesLow = 215,
					seriesHigh = 230,
					model = ChartEntryModelProducer(listOf(
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
				date = LocalDate.parse("2023-10-01"),
				total = 880,
				scores = null,
				itemSize = SeriesItemSize.DEFAULT,
				onClick = {},
			)

			Divider(modifier = Modifier.padding(start = 16.dp))

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