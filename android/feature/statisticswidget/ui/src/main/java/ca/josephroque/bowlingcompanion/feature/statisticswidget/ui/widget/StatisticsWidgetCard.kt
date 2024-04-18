package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.LoadingState
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.charts.AveragingChart
import ca.josephroque.bowlingcompanion.core.statistics.charts.CountingChart
import ca.josephroque.bowlingcompanion.core.statistics.charts.PercentageChart
import ca.josephroque.bowlingcompanion.core.statistics.models.ChartSize
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetSource
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.R
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.titleResourceId
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.util.UUID

@Composable
fun StatisticsWidgetCard(
	widget: StatisticsWidget,
	chart: StatisticChartContent?,
	chartEntryModelProducer: ChartEntryModelProducer?,
	modifier: Modifier = Modifier,
	onClick: (() -> Unit)? = null,
) {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = colorResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_300,
			),
		),
		modifier = modifier,
		onClick = onClick ?: {},
	) {
		if (chart == null || chartEntryModelProducer == null) {
			LoadingState(
				indicatorColor = colorResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.color.pink_100,
				),
			)
		} else {
			when (chart) {
				is StatisticChartContent.AveragingChart -> AveragingChartWidget(
					widget = widget,
					chart = chart,
					chartEntryModelProducer = chartEntryModelProducer,
				)
				is StatisticChartContent.PercentageChart -> PercentageChartWidget(
					widget = widget,
					chart = chart,
					chartEntryModelProducer = chartEntryModelProducer,
				)
				is StatisticChartContent.CountableChart -> CountingChartWidget(
					widget = widget,
					chart = chart,
					chartEntryModelProducer = chartEntryModelProducer,
				)
				is StatisticChartContent.DataMissing -> DataMissingWidget(chart.id)
				is StatisticChartContent.ChartUnavailable -> UnavailableWidget(chart.id)
			}
		}
	}
}

@Composable
private fun AveragingChartWidget(
	widget: StatisticsWidget,
	chart: StatisticChartContent.AveragingChart,
	chartEntryModelProducer: ChartEntryModelProducer,
) {
	Widget(
		title = stringResource(widget.statistic.titleResourceId),
		footer = {
			Row(
				verticalAlignment = Alignment.CenterVertically,
			) {
				Timeline(
					widget.timeline,
					modifier = Modifier.weight(1f),
				)

				Percentage(chart.data.percentDifferenceOverFullTimeSpan)
			}
		},
	) {
		AveragingChart(
			chartData = chart.data,
			chartModel = chartEntryModelProducer,
			size = ChartSize.COMPACT,
		)
	}
}

@Composable
private fun CountingChartWidget(
	widget: StatisticsWidget,
	chart: StatisticChartContent.CountableChart,
	chartEntryModelProducer: ChartEntryModelProducer,
) {
	Widget(
		title = stringResource(widget.statistic.titleResourceId),
		footer = {
			Timeline(widget.timeline)
		},
	) {
		CountingChart(
			chartData = chart.data,
			chartModel = chartEntryModelProducer,
			size = ChartSize.COMPACT,
		)
	}
}

@Composable
private fun PercentageChartWidget(
	widget: StatisticsWidget,
	chart: StatisticChartContent.PercentageChart,
	chartEntryModelProducer: ChartEntryModelProducer,
) {
	Widget(
		title = stringResource(widget.statistic.titleResourceId),
		footer = {
			Row(
				verticalAlignment = Alignment.CenterVertically,
			) {
				Timeline(
					widget.timeline,
					modifier = Modifier.weight(1f),
				)

				Percentage(chart.data.percentDifferenceOverFullTimeSpan ?: 0.0)
			}
		},
	) {
		PercentageChart(
			chartData = chart.data,
			chartModel = chartEntryModelProducer,
			size = ChartSize.COMPACT,
		)
	}
}

@Composable
private fun DataMissingWidget(statistic: StatisticID) {
	Widget(
		title = stringResource(statistic.titleResourceId),
		subtitle = stringResource(R.string.statistics_widget_chart_data_missing),
		footer = {
			WhatDoesThisMeanFooter()
		},
	) {
	}
}

@Composable
private fun UnavailableWidget(statistic: StatisticID) {
	Widget(
		title = stringResource(statistic.titleResourceId),
		subtitle = stringResource(R.string.statistics_widget_chart_unavailable),
		footer = {
			WhatDoesThisMeanFooter()
		},
	) {
		Spacer(modifier = Modifier.weight(1f))

		Image(
			painterResource(id = R.drawable.ic_exclamation_point),
			contentDescription = null,
			modifier = Modifier
				.size(20.dp)
				.align(Alignment.CenterHorizontally)
				.padding(8.dp),
		)

		Spacer(modifier = Modifier.weight(1f))
	}
}

@Composable
private fun Widget(
	title: String,
	modifier: Modifier = Modifier,
	subtitle: String? = null,
	footer: @Composable ColumnScope.() -> Unit = {},
	content: @Composable ColumnScope.() -> Unit,
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(8.dp),
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.bodySmall,
			fontWeight = FontWeight.Bold,
			color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.white),
		)

		if (subtitle != null) {
			Text(
				text = subtitle,
				style = MaterialTheme.typography.labelSmall,
				color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.white),
				modifier = Modifier.padding(top = 2.dp),
			)
		}

		Column(modifier = Modifier.weight(1f)) {
			content()
		}

		footer()
	}
}

@Composable
private fun WhatDoesThisMeanFooter() {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Icon(
			Icons.Default.Info,
			contentDescription = null,
			tint = colorResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.color.warning_container,
			),
		)

		Text(
			text = stringResource(R.string.statistics_widget_what_does_this_mean),
			style = MaterialTheme.typography.labelSmall,
			color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.white),
		)
	}
}

@Composable
private fun Timeline(timeline: StatisticsWidgetTimeline, modifier: Modifier = Modifier) {
	Text(
		text = stringResource(timeline.titleResourceId()),
		style = MaterialTheme.typography.labelSmall,
		color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.white),
		modifier = modifier,
	)
}

@Composable
private fun Percentage(value: Double, modifier: Modifier = Modifier) {
	Text(
		text = formatPercentage(value),
		style = MaterialTheme.typography.labelSmall,
		color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.white),
		modifier = modifier,
	)
}

@Composable
private fun formatPercentage(value: Double): String {
	val formatted = stringResource(R.string.statistics_widget_percentage, value)
	return if (formatted == "0%" || value < 0) {
		formatted
	} else {
		"+$formatted"
	}
}

@Preview
@Composable
private fun StatisticsWidgetCardPreview() {
	StatisticsWidgetCard(
		widget = StatisticsWidget(
			id = UUID.randomUUID(),
			source = StatisticsWidgetSource.Bowler(UUID.randomUUID()),
			priority = 0,
			statistic = StatisticID.ACES,
			timeline = StatisticsWidgetTimeline.ONE_YEAR,
			context = "",
		),
		chart = StatisticChartContent.CountableChart(
			data = CountableChartData(
				id = StatisticID.ACES,
				entries = emptyList(),
				isAccumulating = true,
			),
		),
		chartEntryModelProducer = ChartEntryModelProducer(),
		modifier = Modifier.aspectRatio(2f),
	)
}
