package ca.josephroque.bowlingcompanion.core.charts

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.DefaultColors
import com.patrykandpatrick.vico.core.DefaultDimens
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders

@Composable
fun ProvideDefaultChartStyle(content: @Composable () -> Unit) {
	ProvideChartStyle(
		chartStyle = rememberChartStyle(
			columnChartColors = listOf(
				colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_300),
			),
			lineChartColors = listOf(
				Pair(
					colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.yellow_700),
					colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.pink_500),
				),
			),
		),
	) {
		content()
	}
}

@Composable
fun ProvideWidgetChartStyle(context: @Composable () -> Unit) {
	ProvideChartStyle(
		chartStyle = rememberChartStyle(
			columnChartColors = listOf(
				colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_300),
			),
			lineChartColors = listOf(
				Pair(
					colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.pink_200),
					null,
				),
			),
		),
	) {
		context()
	}
}

@Composable
fun rememberChartStyle(
	columnChartColors: List<Color> = emptyList(),
	lineChartColors: List<Pair<Color, Color?>> = emptyList(),
	axisColor: Color? = null,
): ChartStyle {
	val isSystemInDarkTheme = isSystemInDarkTheme()
	return remember(columnChartColors, lineChartColors, isSystemInDarkTheme) {
		val defaultColors = if (isSystemInDarkTheme) DefaultColors.Dark else DefaultColors.Light
		ChartStyle(
			ChartStyle.Axis(
				axisLabelColor = axisColor ?: Color(defaultColors.axisLabelColor),
				axisGuidelineColor = axisColor ?: Color(defaultColors.axisGuidelineColor),
				axisLineColor = axisColor ?: Color(defaultColors.axisLineColor),
			),
			ChartStyle.ColumnChart(
				columnChartColors.map { columnChartColor ->
					LineComponent(
						columnChartColor.toArgb(),
						DefaultDimens.COLUMN_WIDTH,
						Shapes.roundedCornerShape(DefaultDimens.COLUMN_ROUNDNESS_PERCENT),
					)
				},
			),
			ChartStyle.LineChart(
				lines = lineChartColors.map { lineColors ->
					LineChart.LineSpec(
						lineColor = lineColors.first.toArgb(),
						lineBackgroundShader = lineColors.second?.let {
							DynamicShaders.fromBrush(
								Brush.verticalGradient(
									listOf(
										it.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
										it.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END),
									),
								),
							)
						},
					)
				},
			),
			ChartStyle.Marker(),
			Color(defaultColors.elevationOverlayColor),
		)
	}
}
