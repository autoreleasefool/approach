package ca.josephroque.bowlingcompanion.feature.seriesdetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

@Composable
fun SeriesDetailsHeader(
	numberOfGames: Int,
	seriesTotal: Int,
	seriesLow: Int?,
	seriesHigh: Int?,
	isShowingPlaceholder: Boolean,
	scores: ChartEntryModelProducer?,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier.alignBy(FirstBaseline),
			) {
				Text(
					text = pluralStringResource(R.plurals.games_count, numberOfGames, numberOfGames),
					style = MaterialTheme.typography.titleMedium,
				)

				if (isShowingPlaceholder) {
					Text(
						text = stringResource(R.string.series_details_placeholder),
						style = MaterialTheme.typography.bodyMedium,
						fontStyle = FontStyle.Italic,
					)
				} else {
					if (numberOfGames > 1 && seriesLow != null && seriesHigh != null) {
						Column {
							Text(
								text = stringResource(R.string.series_details_high_game, seriesHigh),
								style = MaterialTheme.typography.bodyLarge,
								fontStyle = FontStyle.Italic,
							)

							Text(
								text = stringResource(R.string.series_details_low_game, seriesLow),
								style = MaterialTheme.typography.bodyLarge,
								fontStyle = FontStyle.Italic,
							)
						}
					}
				}
			}

			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp),
				horizontalAlignment = Alignment.End,
				modifier = Modifier.alignBy(FirstBaseline),
			) {
				Text(
					text = stringResource(R.string.series_details_total),
					style = MaterialTheme.typography.bodyMedium,
				)

				Text(
					seriesTotal.toString(),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Black,
				)
			}
		}

		if (scores != null) {
			ScoreChart(scores)

			Divider(
				thickness = 8.dp,
				modifier = Modifier.padding(vertical = 8.dp),
			)
		}
	}
}

@Composable
private fun ScoreChart(scores: ChartEntryModelProducer) {
	ProvideChartStyle(
		chartStyle = rememberChartStyle(chartColors = listOf(colorResource(RCoreDesign.color.purple_300))),
	) {
		Chart(
			chart = lineChart(
				axisValuesOverrider = AxisValuesOverrider.fixed(
					// FIXME: Decide how the yRange is calculated for iOS and Android
					// iOS currently sets the yRange to 0..450
					// Android sets the range to minScore..maxScore
					// Results in iOS having less dramatic charts, but are relative to one another
					// Android has greater variation in charts, but cannot be compared to one another
					minY = 0F,
					maxY = 450F,
				),
			),
			chartModelProducer = scores,
			horizontalLayout = HorizontalLayout.FullWidth(),
			modifier = Modifier
				.fillMaxWidth()
				.height(96.dp),
		)
	}
}

@Preview
@Composable
private fun SeriesDetailsHeaderPreview() {
	Surface {
		SeriesDetailsHeader(
			numberOfGames = 4,
			seriesTotal = 880,
			seriesLow = 200,
			seriesHigh = 280,
			isShowingPlaceholder = false,
			scores = ChartEntryModelProducer(listOf(
				entryOf(0, 200),
				entryOf(1, 240),
				entryOf(2, 215),
				entryOf(3, 225),
			)),
		)
	}
}