package ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.format
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.PickableResourceCard
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.R

@Composable
fun SourcePicker(state: SourcePickerUiState, onAction: (SourcePickerUiAction) -> Unit, modifier: Modifier = Modifier) {
	Column(
		modifier = modifier.padding(bottom = 16.dp),
	) {
		if (state.isTeamsEnabled) {
			PickableResourceCard(
				resourceName = stringResource(R.string.statistics_filter_team),
				selectedName = when (state.source) {
					is TrackableFilter.SourceSummaries.Team -> state.source.team.name
					is TrackableFilter.SourceSummaries.Bowler, null -> stringResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.string.none,
					)
				},
				onClick = { onAction(SourcePickerUiAction.TeamClicked) },
			)

			HorizontalDivider(thickness = 8.dp)
		}

		PickableResourceCard(
			resourceName = stringResource(R.string.statistics_filter_bowler),
			selectedName = when (state.source) {
				is TrackableFilter.SourceSummaries.Team -> stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string.none,
				)
				is TrackableFilter.SourceSummaries.Bowler -> state.source.bowler.name
				null -> stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.all)
			},
			onClick = { onAction(SourcePickerUiAction.BowlerClicked) },
		)

		when (state.source) {
			is TrackableFilter.SourceSummaries.Team, null -> Unit
			is TrackableFilter.SourceSummaries.Bowler -> {
				HorizontalDivider()

				PickableResourceCard(
					resourceName = stringResource(R.string.statistics_filter_league),
					selectedName = state.source.league?.name ?: stringResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.string.all,
					),
					onClick = { onAction(SourcePickerUiAction.LeagueClicked) },
				)

				HorizontalDivider()

				PickableResourceCard(
					resourceName = stringResource(R.string.statistics_filter_series),
					selectedName = state.source.series?.date?.format("yyyy-MM-dd")
						?: stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.all),
					onClick = { onAction(SourcePickerUiAction.SeriesClicked) },
					enabled = state.source.league != null,
				)

				HorizontalDivider()

				val gameOrdinal = state.source.game?.index?.plus(1)
				PickableResourceCard(
					resourceName = stringResource(R.string.statistics_filter_game),
					selectedName = if (gameOrdinal == null) {
						stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.all)
					} else {
						stringResource(
							ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal,
							gameOrdinal,
						)
					},
					onClick = { onAction(SourcePickerUiAction.GameClicked) },
					enabled = state.source.series != null,
					modifier = Modifier.padding(bottom = 16.dp),
				)
			}
		}
	}
}
