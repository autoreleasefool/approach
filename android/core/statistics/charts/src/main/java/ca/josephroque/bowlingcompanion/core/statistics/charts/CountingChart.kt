package ca.josephroque.bowlingcompanion.core.statistics.charts

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.designsystem.R
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.entry.ChartEntryModel

@Composable
fun CountingChart(
	chartData: ChartEntryModel,
) {
	ProvideChartStyle(
		chartStyle = rememberChartStyle(
			chartColors = listOf(colorResource(R.color.purple_300))
		),
	) {
		Chart(
			chart = lineChart(),
			model = chartData,
			horizontalLayout = HorizontalLayout.FullWidth(),
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight()
		)
	}
}