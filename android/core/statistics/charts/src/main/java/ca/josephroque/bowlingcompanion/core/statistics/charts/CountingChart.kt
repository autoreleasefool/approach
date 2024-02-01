package ca.josephroque.bowlingcompanion.core.statistics.charts

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.designsystem.R
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.getModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.models.ChartEntryKey
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartEntry
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
import kotlin.math.roundToLong

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
	val data = CountableChartData(
		id = StatisticID.TOTAL_ROLLS,
		isAccumulating = false,
		entries = listOf(
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2018-10-03"), days=24), value=655),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2018-10-27"), days=24), value=1258),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2018-11-20"), days=24), value=1922),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2018-12-14"), days=24), value=2605),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-01-07"), days=24), value=2876),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-01-31"), days=24), value=3600),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-02-24"), days=24), value=4164),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-03-20"), days=24), value=4805),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-04-13"), days=24), value=5333),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-05-07"), days=24), value=5461),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-05-31"), days=24), value=5522),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-06-24"), days=24), value=5584),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-07-18"), days=24), value=5735),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-08-11"), days=24), value=5799),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-09-04"), days=24), value=5862),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-09-28"), days=24), value=5931),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-10-22"), days=24), value=6562),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-11-15"), days=24), value=6935),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2020-01-26"), days=24), value=7685),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2019-12-09"), days=24), value=7413),
			CountableChartEntry(key=ChartEntryKey.Date(LocalDate.parse("2020-01-02"), days=24), value=7595),
		)
	)

	Surface {
		CountingChart(
			chartData = data,
			chartModel = ChartEntryModelProducer(data.getModelEntries())
		)
	}
}