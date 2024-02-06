package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.stub.BowlerSummaryStub
import ca.josephroque.bowlingcompanion.core.model.stub.GameListItemStub
import ca.josephroque.bowlingcompanion.core.model.ui.GameRow
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun GamesSettings(
	state: GamesSettingsUiState,
	onAction: (GamesSettingsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val reorderableState = rememberReorderableLazyListState(
		onMove = { from, to ->
			onAction(GamesSettingsUiAction.BowlerMoved(from.index, to.index))
		},
	)

	LazyColumn(
		state = reorderableState.listState,
		modifier = modifier
			.reorderable(reorderableState)
			.detectReorderAfterLongPress(reorderableState),
	) {
		if (state.bowlers.size > 1) {
			header(R.string.game_settings_bowlers)

			// If number of items before reorderable list changes,
			// GamesSettingsViewModel#moveBowler must be updated
			items(
				state.bowlers,
				key = { it.id },
			) { bowler ->
				ReorderableItem(
					reorderableState = reorderableState,
					key = bowler.id,
				) { _ ->
					Bowler(
						bowler = bowler,
						isSelected = state.currentBowlerId == bowler.id,
						onAction = onAction,
					)
				}
			}

			item {
				Row(
					modifier = Modifier
						.padding(top = 8.dp, bottom = 8.dp, end = 16.dp)
				) {
					Spacer(modifier = Modifier.weight(1f))
					Text(
						text = "Drag to reorder",
						style = MaterialTheme.typography.labelMedium,
					)
				}
			}

			item {
				Divider(modifier = Modifier.padding(vertical = 8.dp))
			}
		}

		header(R.string.game_settings_games)

		items(
			state.games,
			key = { it.id },
		) {
			val isSelected = state.currentGameId == it.id
			Game(
				game = it,
				isSelected = isSelected,
				onAction = onAction,
			)
		}
	}
}

@Composable
private fun Bowler(
	bowler: BowlerSummary,
	isSelected: Boolean,
	onAction: (GamesSettingsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.background(MaterialTheme.colorScheme.surface)
			.fillMaxWidth()
			.clickable(onClick = { onAction(GamesSettingsUiAction.BowlerClicked(bowler)) })
			.padding(horizontal = 16.dp, vertical = 8.dp),
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

		Icon(
			Icons.Default.Menu,
			contentDescription = null,
		)
	}
}

@Composable
private fun Game(
	game: GameListItem,
	isSelected: Boolean,
	onAction: (GamesSettingsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
		modifier = modifier
			.selectable(
				selected = isSelected,
				onClick = { onAction(GamesSettingsUiAction.GameClicked(game)) },
				role = Role.RadioButton,
			)
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 8.dp),
		) {
			RadioButton(
				selected = isSelected,
				onClick = null,
				modifier = Modifier.size(24.dp),
			)

			GameRow(
				index = game.index,
				score = game.score,
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
		val games = GameListItemStub.list()

		GamesSettings(
			state = GamesSettingsUiState(
				currentBowlerId = bowlers.first().id,
				bowlers = bowlers,
				currentGameId = games.first().id,
				games = games,
			),
			onAction = {},
		)
	}
}