package ca.josephroque.bowlingcompanion.feature.sharing.ui.series

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.common.utils.range
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.ShareableSeries
import ca.josephroque.bowlingcompanion.core.model.charts.ui.SeriesScoreChart
import ca.josephroque.bowlingcompanion.feature.sharing.ui.R
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance
import ca.josephroque.bowlingcompanion.feature.sharing.ui.components.ChartLabel
import ca.josephroque.bowlingcompanion.feature.sharing.ui.components.ChartLabelStyle
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.datetime.LocalDate

@Composable
fun ShareableSeriesImage(
	series: ShareableSeries,
	configuration: SeriesSharingConfigurationUiState,
	modifier: Modifier = Modifier,
) {

	Box(
		modifier = modifier
			.background(
				when (configuration.appearance) {
					SharingAppearance.Light -> Color.White
					SharingAppearance.Dark -> Color.Black
				}
			),
	) {
		ScoreChart(
			series = series,
			configuration = configuration,
		)

		Column(modifier = Modifier.padding(16.dp)) {
			BowlerPropertiesLabels(
				series = series,
				configuration = configuration,
			)

			Spacer(modifier = Modifier.defaultMinSize(minHeight = 16.dp))

			if (configuration.isSummaryChecked) {
				SeriesSummaryLabels(
					series = series,
					configuration = configuration,
				)
			}
		}
	}
}

@Composable
private fun ScoreChart(
	series: ShareableSeries,
	configuration: SeriesSharingConfigurationUiState,
) {
	val scoresModel = remember(series.scores) {
		ChartEntryModelProducer(
			series.scores.mapIndexed { index, score ->
				entryOf(x = index.toFloat(), y = score.toFloat())
			}
		)
	}

	val lineColor = when (configuration.appearance) {
		SharingAppearance.Light -> colorResource(R.color.line_stroke_light)
		SharingAppearance.Dark -> colorResource(R.color.line_stroke_dark)
	}

	val areaColor = when (configuration.appearance) {
		SharingAppearance.Light -> colorResource(R.color.area_background_light)
		SharingAppearance.Dark -> colorResource(R.color.area_background_dark)
	}

	val chartColors = remember(configuration.appearance) {
		listOf(Pair(lineColor, areaColor))
	}

	BoxWithConstraints {
		val chartWidth = maxWidth

		ProvideChartStyle(
			rememberChartStyle(lineChartColors = chartColors),
		) {
			Chart(
				chartScrollState = rememberChartScrollSpec(isScrollEnabled = false),
				chart = lineChart(
					spacing = chartWidth / series.scores.size.toFloat(),
					axisValuesOverrider = AxisValuesOverrider.fixed(
						minY = configuration.chartRange.last.toFloat(),
						maxY = configuration.chartRange.first.toFloat(),
					),
				),
				runInitialAnimation = false,
				chartModelProducer = scoresModel,
				horizontalLayout = HorizontalLayout.FullWidth(),
				modifier = Modifier.fillMaxWidth(),
			)
		}
	}
}

@Composable
private fun BowlerPropertiesLabels(
	series: ShareableSeries,
	configuration: SeriesSharingConfigurationUiState,
) {
	if (configuration.isDateChecked) {
		ChartLabel(
			icon = rememberVectorPainter(Icons.Default.DateRange),
			title = series.properties.date.simpleFormat(),
			style = ChartLabelStyle.TITLE,
			appearance = configuration.appearance,
		)
	}

	if (configuration.isBowlerChecked) {
		ChartLabel(
			icon = rememberVectorPainter(Icons.Default.Person),
			title = series.properties.bowlerName,
			style = ChartLabelStyle.PLAIN,
			appearance = configuration.appearance,
			modifier = Modifier.padding(top = 8.dp),
		)
	}

	if (configuration.isLeagueChecked) {
		ChartLabel(
			icon = rememberVectorPainter(Icons.Default.Refresh),
			title = series.properties.leagueName,
			style = ChartLabelStyle.PLAIN,
			appearance = configuration.appearance,
			modifier = Modifier.padding(top = 8.dp),
		)
	}

	Text(
		stringResource(R.string.sharing_made_with_tryapproach),
		style = MaterialTheme.typography.labelSmall,
		color = when (configuration.appearance) {
			SharingAppearance.Dark -> Color.White.copy(alpha = 0.7f)
			SharingAppearance.Light -> Color.Black.copy(alpha = 0.7f)
		},
		modifier = Modifier.padding(top = 4.dp),
	)
}

@Composable
private fun SeriesSummaryLabels(
	series: ShareableSeries,
	configuration: SeriesSharingConfigurationUiState,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		ChartLabel(
			icon = rememberVectorPainter(Icons.Default.Check),
			title = stringResource(R.string.sharing_series_total_label, series.properties.total),
			style = ChartLabelStyle.SMALL,
			appearance = configuration.appearance,
		)

		val highScore = series.scores.maxOrNull()
		if (configuration.isHighScoreChecked && highScore != null) {
			ChartLabel(
				icon = rememberVectorPainter(Icons.Default.KeyboardArrowUp),
				title = stringResource(R.string.sharing_series_high_score_label, highScore),
				style = ChartLabelStyle.SMALL,
				appearance = configuration.appearance,
			)
		}

		val lowScore = series.scores.minOrNull()
		if (configuration.isLowScoreChecked && lowScore != null) {
			ChartLabel(
				icon = rememberVectorPainter(Icons.Default.KeyboardArrowDown),
				title = stringResource(R.string.sharing_series_low_score_label, lowScore),
				style = ChartLabelStyle.SMALL,
				appearance = configuration.appearance,
			)
		}
	}
}

private class AppearancePreviewParameterProvider : PreviewParameterProvider<SharingAppearance> {
	override val values = sequenceOf(SharingAppearance.Light, SharingAppearance.Dark)
}

@Preview
@Composable
private fun ShareableSeriesImagePreview(
	@PreviewParameter(AppearancePreviewParameterProvider::class) appearance: SharingAppearance,
) {
	Surface {
		ShareableSeriesImage(
			series = ShareableSeries(
				properties = ShareableSeries.Properties(
					id = SeriesID.randomID(),
					date = LocalDate(2025, 2, 22),
					total = 900,
					bowlerName = "Joseph",
					leagueName = "Majors",
				),
				scores = listOf(300, 200, 400),
			),
			configuration = SeriesSharingConfigurationUiState(
				isBowlerChecked = true,
				isDateChecked = true,
				isLeagueChecked = true,
				isLowScoreChecked = true,
				isHighScoreChecked = true,
				appearance = appearance,
			),
		)
	}
}