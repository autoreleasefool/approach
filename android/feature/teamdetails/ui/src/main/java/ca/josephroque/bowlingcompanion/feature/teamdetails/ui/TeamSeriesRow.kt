package ca.josephroque.bowlingcompanion.feature.teamdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.designsystem.components.MeasureUnconstrainedViewWidth
import ca.josephroque.bowlingcompanion.core.model.charts.ui.SeriesChartData
import ca.josephroque.bowlingcompanion.core.model.charts.ui.SeriesScoreChart
import ca.josephroque.bowlingcompanion.core.model.ui.SeriesRow
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.datetime.LocalDate

data class TeamMemberSeriesChartData(val name: String, val chart: SeriesChartData)

@Composable
fun TeamSeriesRow(
	date: LocalDate,
	total: Int,
	teamChart: SeriesChartData?,
	memberCharts: List<TeamMemberSeriesChartData>?,
	modifier: Modifier = Modifier,
) {
	Box(
		contentAlignment = Alignment.BottomCenter,
		modifier = modifier
			.fillMaxWidth()
			.widthIn(max = 600.dp),
	) {
		Column {
			SeriesRow(
				date = date,
				total = total,
				modifier = Modifier.padding(16.dp),
			)

			teamChart?.let {
				SeriesScoreChart(
					numberOfGames = it.numberOfGames,
					scoreRange = it.scoreRange,
					model = it.model,
					modifier = Modifier.height(48.dp),
				)
			}

			memberCharts?.let {
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier
						.horizontalScroll(rememberScrollState())
						.padding(vertical = 8.dp, horizontal = 16.dp),
				) {
					memberCharts.forEach { memberChart ->
						MeasureUnconstrainedViewWidth(
							viewToMeasure = {
								Text(text = memberChart.name)
							},
						) { measuredWidth ->
							Box(
								modifier = Modifier
									.border(
										width = 1.dp,
										color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
										shape = RoundedCornerShape(16.dp),
									)
									.clip(RoundedCornerShape(16.dp))
									.padding(start = 8.dp)
									.padding(vertical = 4.dp)
									.widthIn(min = measuredWidth + 48.dp),
							) {
								SeriesScoreChart(
									chartStyle = rememberChartStyle(
										lineChartColors = listOf(
											Pair(
												colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.yellow_700),
												colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.yellow_700),
											),
										),
									),
									numberOfGames = memberChart.chart.numberOfGames,
									scoreRange = memberChart.chart.scoreRange,
									model = memberChart.chart.model,
									modifier = Modifier
										.width(44.dp)
										.height(24.dp)
										.offset(x = measuredWidth + 4.dp),
								)

								Box(
									modifier = Modifier
										.offset(x = measuredWidth + 4.dp)
										.background(
											Brush.horizontalGradient(
												0f to MaterialTheme.colorScheme.surface,
												1f to Color.Transparent,
											),
										)
										.width(8.dp)
										.height(24.dp),
								)

								Text(
									text = memberChart.name,
									modifier = Modifier
										.align(Alignment.CenterStart),
								)
							}
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun TeamSeriesRowPreview() {
	Surface {
		Column {
			TeamSeriesRow(
				date = LocalDate.parse("2023-09-24"),
				total = 1760,
				teamChart = SeriesChartData(
					numberOfGames = 4,
					scoreRange = 430..460,
					model = ChartEntryModelProducer(
						listOf(
							entryOf(0, 440),
							entryOf(1, 460),
							entryOf(2, 430),
							entryOf(3, 450),
						),
					),
				),
				memberCharts = listOf(
					TeamMemberSeriesChartData(
						name = "Joseph",
						chart = SeriesChartData(
							numberOfGames = 4,
							scoreRange = 215..230,
							model = ChartEntryModelProducer(
								listOf(
									entryOf(0, 220),
									entryOf(1, 230),
									entryOf(2, 215),
									entryOf(3, 225),
								),
							),
						),
					),
					TeamMemberSeriesChartData(
						name = "Sarah",
						chart = SeriesChartData(
							numberOfGames = 4,
							scoreRange = 215..230,
							model = ChartEntryModelProducer(
								listOf(
									entryOf(0, 220),
									entryOf(1, 230),
									entryOf(2, 215),
									entryOf(3, 225),
								),
							),
						),
					),
					TeamMemberSeriesChartData(
						name = "Jordan",
						chart = SeriesChartData(
							numberOfGames = 4,
							scoreRange = 215..230,
							model = ChartEntryModelProducer(
								listOf(
									entryOf(0, 220),
									entryOf(1, 230),
									entryOf(2, 215),
									entryOf(3, 225),
								),
							),
						),
					),
				),
			)
		}
	}
}
