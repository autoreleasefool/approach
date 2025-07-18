package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.SectionFooter
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSwitch
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.stub.BowlerSummaryStub
import ca.josephroque.bowlingcompanion.core.model.stub.GameStub
import ca.josephroque.bowlingcompanion.core.model.stub.TeamStub
import ca.josephroque.bowlingcompanion.core.model.ui.GameRow
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun GamesSettings(
	state: GamesSettingsUiState,
	onAction: (GamesSettingsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val hapticFeedback = LocalHapticFeedback.current

	val lazyListState = rememberLazyListState()
	val reorderableLazyListState = rememberReorderableLazyListState(
		lazyListState = lazyListState,
		onMove = { from, to ->
			onAction(GamesSettingsUiAction.BowlerMoved(from.index, to.index, 2))

			hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
		},
	)

	LazyColumn(
		state = lazyListState,
		modifier = modifier
	) {
		if (state.bowlerSettings.bowlers.size > 1) {
			item {
				FormSwitch(
					titleResourceId = R.string.game_settings_show_team_scores_in_game_details,
					isChecked = state.teamSettings.isShowingTeamScoresInGameDetails,
					onCheckChanged = { isChecked ->
						onAction(GamesSettingsUiAction.ShowTeamScoresInGameDetailsChanged(isChecked))
					},
					modifier = Modifier.padding(horizontal = 16.dp),
				)

				SectionFooter(
					footer = stringResource(R.string.game_settings_team_scores_impacts_performance),
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 8.dp),
				)

				HorizontalDivider()
			}

			if (state.teamSettings.team == null) {
				header(R.string.game_settings_bowlers)
			} else {
				header(state.teamSettings.team.name)
			}

			// If number of items before reorderable list changes,
			// reorderableLazyListState#onMove must be updated
			items(
				state.bowlerSettings.bowlers,
				key = { it.id },
			) { bowler ->
				ReorderableItem(
					state = reorderableLazyListState,
					key = bowler.id,
				) { isDragging ->
					val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

					Surface(shadowElevation = elevation) {
						Bowler(
							bowler = bowler,
							isSelected = state.bowlerSettings.currentBowlerId == bowler.id,
							onAction = onAction,
						)
					}
				}
			}

			item {
				Row(
					modifier = Modifier
						.padding(top = 8.dp, bottom = 8.dp, end = 16.dp),
				) {
					Spacer(modifier = Modifier.weight(1f))
					Text(
						text = "Drag to reorder",
						style = MaterialTheme.typography.labelMedium,
					)
				}
			}

			item {
				HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
			}
		}

		header(R.string.game_settings_games)

		items(
			state.gameSettings.games,
			key = { it.id },
		) {
			val isSelected = state.gameSettings.currentGameId == it.id
			Game(
				game = it,
				isSelected = isSelected,
				isMultipleBowlers = state.bowlerSettings.bowlers.size > 1,
				onAction = onAction,
			)
		}
	}
}

@Composable
private fun ReorderableCollectionItemScope.Bowler(
	bowler: BowlerSummary,
	isSelected: Boolean,
	onAction: (GamesSettingsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val hapticFeedback = LocalHapticFeedback.current

	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = { onAction(GamesSettingsUiAction.BowlerClicked(bowler)) })
			.padding(16.dp),
	) {
		if (isSelected) {
			Icon(
				Icons.Default.PlayArrow,
				contentDescription = stringResource(R.string.cd_game_settings_selected),
				tint = MaterialTheme.colorScheme.primary,
				modifier = Modifier.size(24.dp),
			)
		} else {
			Spacer(modifier = Modifier.size(24.dp))
		}

		Text(
			text = bowler.name,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.weight(1f),
		)

		IconButton(
			modifier = Modifier.longPressDraggableHandle(
				onDragStarted = {
					hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
				},
				onDragStopped = {
					hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
				},
			),
			onClick = {},
		) {
			Icon(
				Icons.Default.Menu,
				contentDescription = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.cd_reorder),
			)
		}
	}
}

@Composable
private fun Game(
	game: GameListItem,
	isMultipleBowlers: Boolean,
	isSelected: Boolean,
	onAction: (GamesSettingsUiAction) -> Unit,
) {
	Surface(
		color = if (isSelected) {
			MaterialTheme.colorScheme.primaryContainer
		} else {
			MaterialTheme.colorScheme.surface
		},
		modifier = Modifier
			.selectable(
				selected = isSelected,
				onClick = { onAction(GamesSettingsUiAction.GameClicked(game)) },
				role = Role.RadioButton,
			),
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			RadioButton(
				selected = isSelected,
				onClick = null,
				modifier = Modifier.size(24.dp),
			)

			GameRow(
				index = game.index,
				score = if (isMultipleBowlers) null else game.score,
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@Preview
@Composable
private fun GamesSettingsPreview() {
	Surface {
		val bowlers = BowlerSummaryStub.list()
		val games = GameStub.list()
		val team = TeamStub.single()

		GamesSettings(
			state = GamesSettingsUiState(
				teamSettings = GamesSettingsUiState.TeamSettings(
					team = team,
					isShowingTeamScoresInGameDetails = false,
				),
				bowlerSettings = GamesSettingsUiState.BowlerSettings(
					currentBowlerId = bowlers.first().id,
					bowlers = bowlers,
				),
				gameSettings = GamesSettingsUiState.GameSettings(
					currentGameId = games.first().id,
					games = games,
				),
			),
			onAction = {},
		)
	}
}
