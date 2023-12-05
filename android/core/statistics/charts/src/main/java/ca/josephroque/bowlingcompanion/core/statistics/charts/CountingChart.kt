package ca.josephroque.bowlingcompanion.core.statistics.charts

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import ca.josephroque.bowlingcompanion.core.charts.rememberChartStyle
import ca.josephroque.bowlingcompanion.core.designsystem.R
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartData
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

@Composable
fun CountingChart(
	chartData: CountableChartData,
	chartModel: ChartEntryModelProducer,
) {
	Card {
		ProvideChartStyle(
			chartStyle = rememberChartStyle(
				chartColors = listOf(colorResource(R.color.purple_300))
			),
		) {
			Chart(
				chart = if (chartData.isAccumulating) lineChart() else columnChart(),
				chartModelProducer = chartModel,
				horizontalLayout = HorizontalLayout.FullWidth(),
				modifier = Modifier
					.fillMaxWidth()
					.fillMaxHeight()
			)
		}
	}
}