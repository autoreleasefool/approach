package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormRadioGroup
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.PickableResourceCard
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.ListSectionFooter
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.R
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.titleResourceId
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.widget.StatisticsWidgetCard

@Composable
fun StatisticsWidgetEditor(
	state: StatisticsWidgetEditorUiState,
	onAction: (StatisticsWidgetEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	BoxWithConstraints(
		modifier = modifier.fillMaxSize(),
	) {
		val shouldLimitSize = maxWidth >= 500.dp

		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState()),
		) {
			if (state.widget != null) {
				StatisticsWidgetCard(
					widget = state.widget,
					chart = state.preview?.chart,
					chartEntryModelProducer = state.preview?.modelProducer,
					modifier = Modifier
						.then(if (shouldLimitSize) Modifier.requiredWidthIn(max = 320.dp) else Modifier)
						.aspectRatio(2f)
						.padding(horizontal = 16.dp),
				)

				Text(
					text = stringResource(R.string.statistics_widget_editor_preview),
					style = MaterialTheme.typography.labelSmall,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(top = 4.dp, bottom = 8.dp)
						.align(Alignment.CenterHorizontally),
				)

				HorizontalDivider()
			}

			ListSectionFooter(
				footer = stringResource(R.string.statistics_widget_editor_filter_description),
			)

			PickableResourceCard(
				resourceName = stringResource(R.string.statistics_widget_editor_filter_bowler),
				selectedName = state.bowler?.name ?: stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string.none,
				),
				onClick = { onAction(StatisticsWidgetEditorUiAction.BowlerClicked) },
			)

			PickableResourceCard(
				resourceName = stringResource(R.string.statistics_widget_editor_filter_league),
				selectedName = state.league?.name ?: stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string.none,
				),
				onClick = { onAction(StatisticsWidgetEditorUiAction.LeagueClicked) },
				enabled = state.bowler != null,
			)

			HorizontalDivider()

			PickableResourceCard(
				resourceName = stringResource(R.string.statistics_widget_editor_statistic),
				selectedName = stringResource(state.statistic.id.titleResourceId),
				onClick = { onAction(StatisticsWidgetEditorUiAction.StatisticClicked) },
			)

			HorizontalDivider()

			FormRadioGroup(
				titleResourceId = R.string.statistics_widget_timeline,
				subtitleResourceId = R.string.statistics_widget_editor_timeline_description,
				options = StatisticsWidgetTimeline.entries.toTypedArray(),
				selected = state.timeline,
				titleForOption = {
					if (it == null) {
						""
					} else {
						stringResource(it.titleResourceId())
					}
				},
				onOptionSelected = {
					it?.let {
						onAction(
							StatisticsWidgetEditorUiAction.TimelineSelected(it),
						)
					}
				},
				modifier = Modifier.padding(top = 16.dp),
			)
		}
	}
}
