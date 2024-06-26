package ca.josephroque.bowlingcompanion.core.statistics.charts

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.charts.rememberEmptyBottomAxis
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.horizontalLayout
import ca.josephroque.bowlingcompanion.core.statistics.models.AveragingChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.ChartEntryKey
import ca.josephroque.bowlingcompanion.core.statistics.models.ChartSize
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.formatter.DecimalFormatAxisValueFormatter
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.extension.copyColor
import kotlin.math.roundToInt
import kotlinx.datetime.LocalDate

@Composable
fun AveragingChart(
	chartData: AveragingChartData,
	chartModel: ChartEntryModelProducer,
	size: ChartSize,
	modifier: Modifier = Modifier,
) {
	Chart(
		chart = lineChart(),
		chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
		chartModelProducer = chartModel,
		horizontalLayout = size.horizontalLayout,
		bottomAxis = when (size) {
			ChartSize.DEFAULT -> rememberBottomAxis(
				labelRotationDegrees = 90f,
				itemPlacer = remember {
					AxisItemPlacer.Horizontal.default(spacing = 2)
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
			)
			ChartSize.COMPACT -> rememberEmptyBottomAxis()
		},
		startAxis = rememberStartAxis(
			label = when (size) {
				ChartSize.COMPACT -> textComponent {
					color = Color.BLACK.copyColor(alpha = 0.6f)
					textSizeSp = 12f
				}
				ChartSize.DEFAULT -> axisLabelComponent()
			},
			itemPlacer = remember {
				AxisItemPlacer.Vertical.default(maxItemCount = chartData.numberOfVerticalTicks)
			},
			valueFormatter = remember {
				DecimalFormatAxisValueFormatter(pattern = "#;-#")
			},
		),
		modifier = modifier.fillMaxSize(),
	)
}
