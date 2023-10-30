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
	locked: GameLockState,
	gameExcludeFromStatistics: ExcludeFromStatistics,
	seriesExcludeFromStatistics: ExcludeFromStatistics,
	leagueExcludeFromStatistics: ExcludeFromStatistics,
	seriesPreBowl: SeriesPreBowl,
	onToggleLock: (Boolean?) -> Unit,
	onToggleExcludeFromStatistics: (Boolean?) -> Unit,
) {
	DetailCard(modifier = modifier) {
		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			LabeledSwitch(
				checked = when (locked) {
					GameLockState.LOCKED -> true
					GameLockState.UNLOCKED -> false
				},
				onCheckedChange = onToggleLock,
				titleResourceId = R.string.game_editor_lock_state_title,
				subtitleResourceId = R.string.game_editor_lock_state_description,
			)

			LabeledSwitch(
				checked = when (leagueExcludeFromStatistics) {
					ExcludeFromStatistics.EXCLUDE -> true
					ExcludeFromStatistics.INCLUDE -> when (seriesPreBowl) {
						SeriesPreBowl.PRE_BOWL -> true
						SeriesPreBowl.REGULAR -> when (seriesExcludeFromStatistics) {
							ExcludeFromStatistics.EXCLUDE -> true
							ExcludeFromStatistics.INCLUDE -> when (gameExcludeFromStatistics) {
								ExcludeFromStatistics.EXCLUDE -> true
								ExcludeFromStatistics.INCLUDE -> false
							}
						}
					}
				},
				onCheckedChange = onToggleExcludeFromStatistics,
				titleResourceId = R.string.game_editor_exclude_from_statistics_title,
				subtitleResourceId = when (leagueExcludeFromStatistics) {
					ExcludeFromStatistics.EXCLUDE -> R.string.game_editor_exclude_from_statistics_league_excluded
					ExcludeFromStatistics.INCLUDE -> when (seriesPreBowl) {
						SeriesPreBowl.PRE_BOWL -> R.string.game_editor_exclude_from_statistics_series_pre_bowl
						SeriesPreBowl.REGULAR -> when (seriesExcludeFromStatistics) {
							ExcludeFromStatistics.EXCLUDE -> R.string.game_editor_exclude_from_statistics_series_excluded
							ExcludeFromStatistics.INCLUDE -> R.string.game_editor_exclude_from_statistics_description
						}
					}
				},
				enabled = when (leagueExcludeFromStatistics) {
					ExcludeFromStatistics.EXCLUDE -> false
					ExcludeFromStatistics.INCLUDE -> when (seriesPreBowl) {
						SeriesPreBowl.PRE_BOWL -> false
						SeriesPreBowl.REGULAR -> when (seriesExcludeFromStatistics) {
							ExcludeFromStatistics.EXCLUDE -> false
							ExcludeFromStatistics.INCLUDE -> true
						}
					}
				}
			)
		}
	}
}

@Preview
@Composable
private fun GamePropertiesCardPreview() {
	GamePropertiesCard(
		locked = GameLockState.LOCKED,
		gameExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
		seriesExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
		leagueExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
		seriesPreBowl = SeriesPreBowl.REGULAR,
		onToggleLock = {},
		onToggleExcludeFromStatistics = {},
	)
}