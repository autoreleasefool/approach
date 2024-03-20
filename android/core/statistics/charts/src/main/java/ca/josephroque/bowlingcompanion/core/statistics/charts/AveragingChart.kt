package ca.josephroque.bowlingcompanion.core.statistics.charts

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.statistics.models.AveragingChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.ChartEntryKey
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.formatter.DecimalFormatAxisValueFormatter
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlin.math.roundToInt
import kotlinx.datetime.LocalDate

@Composable
fun AveragingChart(chartData: AveragingChartData, chartModel: ChartEntryModelProducer) {
	ProvideChartStyle(
		chartStyle = rememberChartStyle(
			chartColors = listOf(
				colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_300),
			),
		),
	) {
		Chart(
			chart = lineChart(),
			chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
			chartModelProducer = chartModel,
			horizontalLayout = HorizontalLayout.FullWidth(),
			bottomAxis = rememberBottomAxis(
				itemPlacer = remember {
					AxisItemPlacer.Horizontal.default(
						spacing = 2,
						offset = 2,
						shiftExtremeTicks = true,
					)
				},
				valueFormatter = remember {
					AxisValueFormatter { value, _ ->
						if (chartData.firstKey is ChartEntryKey.Date) {
							LocalDate.fromEpochDays(value.roundToInt()).toString()
						} else {
							"Game ${value.roundToInt()}"
						}
					}
				},
			),
			startAxis = rememberStartAxis(
				itemPlacer = remember {
					AxisItemPlacer.Vertical.default(maxItemCount = chartData.numberOfVerticalTicks)
				},
				valueFormatter = remember {
					DecimalFormatAxisValueFormatter(pattern = "#;-#")
				}
			),
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight()
				.padding(top = 16.dp),
		)
	}
}
