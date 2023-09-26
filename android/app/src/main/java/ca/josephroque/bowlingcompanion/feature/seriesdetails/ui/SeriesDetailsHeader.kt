package ca.josephroque.bowlingcompanion.feature.seriesdetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlin.math.roundToInt

@Composable
fun SeriesDetailsHeader(
	numberOfGames: Int,
	seriesTotal: Int,
	scores: ChartEntryModel?,
	modifier: Modifier = Modifier,
) {
	ElevatedCard(
		modifier = modifier
			.fillMaxWidth(),
		elevation = CardDefaults.elevatedCardElevation(),
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier.alignBy(FirstBaseline),
			) {
				Text(
					pluralStringResource(R.plurals.games_count, numberOfGames, numberOfGames),
					fontSize = 16.sp,
					fontWeight = FontWeight.Bold,
				)

				if (numberOfGames > 1 && scores != null) {
					Column {
						Text(
							stringResource(R.string.series_details_high_game, scores.maxY.roundToInt()),
							fontSize = 12.sp,
							fontStyle = FontStyle.Italic,
						)

						Text(
							stringResource(R.string.series_details_low_game, scores.minY.roundToInt()),
							fontSize = 12.sp,
							fontStyle = FontStyle.Italic,
						)
					}
				}
			}

			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				horizontalAlignment = Alignment.End,
				modifier = Modifier.alignBy(FirstBaseline),
			) {
				Text(
					stringResource(R.string.series_details_total),
					fontSize = 12.sp,
					fontWeight = FontWeight.Light,
				)

				Text(
					seriesTotal.toString(),
					fontSize = 24.sp,
					fontStyle = FontStyle.Italic,
					fontWeight = FontWeight.Black,
				)
			}
		}

		if (scores != null) {
			ScoreChart(scores)
		}
	}
}

@Composable
fun ScoreChart(scores: ChartEntryModel) {
	ProvideChartStyle(
		chartStyle = rememberChartStyle(chartColors = listOf(colorResource(R.color.purple_300))),
	) {
		Chart(
			chart = lineChart(
				axisValuesOverrider = AxisValuesOverrider.fixed(
					// TODO: Decide how the yRange is calculated for iOS and Android
					// iOS currently sets the yRange to 0..450
					// Android sets the range to minScore..maxScore
					// Results in iOS having less dramatic charts, but are relative to one another
					// Android has greater variation in charts, but cannot be compared to one another
					minY = 0F,
					maxY = 450F,
				),
			),
			model = scores,
			horizontalLayout = HorizontalLayout.FullWidth(),
			modifier = Modifier
				.fillMaxWidth()
				.height(96.dp),
		)
	}
}

@Preview
@Composable
fun SeriesDetailsHeaderPreview() {
	Surface {
		SeriesDetailsHeader(
			numberOfGames = 4,
			seriesTotal = 880,
			scores = entryModelOf(200, 240,215, 225),
			modifier = Modifier.padding(16.dp)
		)
	}
}