package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.LabeledSwitch
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R

@Composable
internal fun GamePropertiesCard(
	state: GameDetailsUiState.GamePropertiesCardUiState,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier,
	) {
		LabeledSwitch(
			checked = when (state.locked) {
				GameLockState.LOCKED -> true
				GameLockState.UNLOCKED -> false
			},
			onCheckedChange = { onAction(GameDetailsUiAction.LockToggled(it)) },
			titleResourceId = R.string.game_editor_lock_state_title,
			subtitleResourceId = R.string.game_editor_lock_state_description,
			compact = true,
		)

		LabeledSwitch(
			checked = when (state.leagueExcludeFromStatistics) {
				ExcludeFromStatistics.EXCLUDE -> true
				ExcludeFromStatistics.INCLUDE -> when (state.seriesPreBowl) {
					SeriesPreBowl.PRE_BOWL -> true
					SeriesPreBowl.REGULAR -> when (state.seriesExcludeFromStatistics) {
						ExcludeFromStatistics.EXCLUDE -> true
						ExcludeFromStatistics.INCLUDE -> when (state.gameExcludeFromStatistics) {
							ExcludeFromStatistics.EXCLUDE -> true
							ExcludeFromStatistics.INCLUDE -> false
						}
					}
				}
			},
			onCheckedChange = { onAction(GameDetailsUiAction.ExcludeFromStatisticsToggled(it)) },
			titleResourceId = R.string.game_editor_exclude_from_statistics_title,
			subtitleResourceId = when (state.leagueExcludeFromStatistics) {
				ExcludeFromStatistics.EXCLUDE -> R.string.game_editor_exclude_from_statistics_league_excluded
				ExcludeFromStatistics.INCLUDE -> when (state.seriesPreBowl) {
					SeriesPreBowl.PRE_BOWL -> R.string.game_editor_exclude_from_statistics_series_pre_bowl
					SeriesPreBowl.REGULAR -> when (state.seriesExcludeFromStatistics) {
						ExcludeFromStatistics.EXCLUDE -> R.string.game_editor_exclude_from_statistics_series_excluded
						ExcludeFromStatistics.INCLUDE -> R.string.game_editor_exclude_from_statistics_description
					}
				}
			},
			enabled = when (state.leagueExcludeFromStatistics) {
				ExcludeFromStatistics.EXCLUDE -> false
				ExcludeFromStatistics.INCLUDE -> when (state.seriesPreBowl) {
					SeriesPreBowl.PRE_BOWL -> false
					SeriesPreBowl.REGULAR -> when (state.seriesExcludeFromStatistics) {
						ExcludeFromStatistics.EXCLUDE -> false
						ExcludeFromStatistics.INCLUDE -> true
					}
				}
			},
			compact = true,
		)
	}
}

@Preview
@Composable
private fun GamePropertiesCardPreview() {
	GamePropertiesCard(
		state = GameDetailsUiState.GamePropertiesCardUiState(
			locked = GameLockState.LOCKED,
			gameExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
			seriesExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
			leagueExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
			seriesPreBowl = SeriesPreBowl.REGULAR,
		),
		onAction = {},
	)
}