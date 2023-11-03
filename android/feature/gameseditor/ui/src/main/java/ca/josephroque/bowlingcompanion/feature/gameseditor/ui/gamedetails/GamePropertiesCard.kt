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
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.DetailCard

@Composable
internal fun GamePropertiesCard(
	modifier: Modifier = Modifier,
	state: GamePropertiesCardUiState,
	onToggleLock: (Boolean?) -> Unit,
	onToggleExcludeFromStatistics: (Boolean?) -> Unit,
) {
	DetailCard(modifier = modifier) {
		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			LabeledSwitch(
				checked = when (state.locked) {
					GameLockState.LOCKED -> true
					GameLockState.UNLOCKED -> false
				},
				onCheckedChange = onToggleLock,
				titleResourceId = R.string.game_editor_lock_state_title,
				subtitleResourceId = R.string.game_editor_lock_state_description,
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
				onCheckedChange = onToggleExcludeFromStatistics,
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
				}
			)
		}
	}
}

data class GamePropertiesCardUiState(
	val locked: GameLockState = GameLockState.LOCKED,
	val gameExcludeFromStatistics: ExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
	val seriesExcludeFromStatistics: ExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
	val leagueExcludeFromStatistics: ExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
	val seriesPreBowl: SeriesPreBowl = SeriesPreBowl.REGULAR,
)

@Preview
@Composable
private fun GamePropertiesCardPreview() {
	GamePropertiesCard(
		state = GamePropertiesCardUiState(
			locked = GameLockState.LOCKED,
			gameExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
			seriesExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
			leagueExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
			seriesPreBowl = SeriesPreBowl.REGULAR,
		),
		onToggleLock = {},
		onToggleExcludeFromStatistics = {},
	)
}