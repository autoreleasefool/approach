package ca.josephroque.bowlingcompanion.core.statistics.charts

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.designsystem.R
import ca.josephroque.bowlingcompanion.core.statistics.charts.stub.CountableChartDataStub
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.getModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.models.ChartEntryKey
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartData
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.formatter.DecimalFormatAxisValueFormatter
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.datetime.LocalDate
import kotlin.math.roundToInt

@Composable
fun CountingChart(
	chartData: CountableChartData,
	chartModel: ChartEntryModelProducer,
) {
	ProvideChartStyle(
		chartStyle = rememberChartStyle(
			chartColors = listOf(colorResource(R.color.purple_300))
		),
	) {
		Chart(
			chart = if (chartData.isAccumulating) lineChart() else columnChart(),
			chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
			chartModelProducer = chartModel,
			horizontalLayout = HorizontalLayout.FullWidth(),
			bottomAxis = rememberBottomAxis(
				itemPlacer = remember {
					AxisItemPlacer.Horizontal.default(
						spacing = 2,
						offset = 2,
						shiftExtremeTicks = true
					)
				},
				valueFormatter = remember {
					AxisValueFormatter { value, _ ->
						if (chartData.firstKey is ChartEntryKey.Date)
							LocalDate.fromEpochDays(value.roundToInt()).toString()
						else
							"Game ${value.roundToInt()}"
					}
				},
			),
			startAxis = rememberStartAxis(
				itemPlacer = remember {
					AxisItemPlacer.Vertical.default(maxItemCount = chartData.numberOfVerticalTicks)
				},
				valueFormatter = remember {
					DecimalFormatAxisValueFormatter(pattern = "#;-#")
//					AxisValueFormatter { value, x ->
//						value.roundToInt().toString()
////						if (value.isFinite() && value.roundToInt().toFloat() == value) value.roundToInt().toString() else ""
//					}
				},
			),
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight()
				.padding(top = 16.dp)
		)
	}
}

@Preview
@Composable
private fun CountingChartPreview() {
	val data = CountableChartDataStub.stub()

	Surface {
		CountingChart(
			chartData = data,
			chartModel = ChartEntryModelProducer(data.getModelEntries())
		)
	}
}