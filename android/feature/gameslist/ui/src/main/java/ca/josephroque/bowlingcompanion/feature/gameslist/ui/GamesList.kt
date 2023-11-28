package ca.josephroque.bowlingcompanion.feature.gameslist.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.ui.GameRow
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.util.UUID

@Composable
fun GamesList(
	state: GamesListUiState,
	onAction: (GamesListUiAction) -> Unit,
	modifier: Modifier = Modifier,
	header: (@Composable LazyItemScope.() -> Unit)? = null,
) {
	state.gameToArchive?.let {
		ArchiveDialog(
			itemName = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal, it.index + 1),
			onArchive = { onAction(GamesListUiAction.ConfirmArchiveClicked) },
			onDismiss = { onAction(GamesListUiAction.DismissArchiveClicked) },
		)
	}

	LazyColumn(modifier = modifier) {
		if (state.list.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.game_list_empty_title,
					icon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.not_found,
					message = R.string.game_list_empty_message,
					action = R.string.game_list_add,
					onActionClick = { onAction(GamesListUiAction.AddGameClicked) },
				)
			}
		} else {
			header?.also {
				item {
					it()
				}
			}

			gamesList(
				list = state.list,
				onGameClick = { onAction(GamesListUiAction.GameClicked(it.id)) },
				onArchiveGame = { onAction(GamesListUiAction.GameArchived(it)) },
			)
		}
	}
}

fun LazyListScope.gamesList(
	list: List<GameListItem>,
	onGameClick: (GameListItem) -> Unit,
	onArchiveGame: (GameListItem) -> Unit,
) {
	itemsIndexed(
		items = list,
		key = { _, game -> game.id },
	) { index, game ->
		val archiveAction = SwipeAction(
			icon = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_archive),
			background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive),
			onSwipe = { onArchiveGame(game) },
		)

		SwipeableActionsBox(
			startActions = listOf(archiveAction),
		) {
			GameRow(
				index = game.index,
				score = game.score,
				onClick = { onGameClick(game) },
			)
		}

		if (index < list.size - 1) {
			Divider(modifier = Modifier.padding(start = 16.dp))
		}
	}
}

@Preview
@Composable
private fun GamesListPreview() {
	Surface {
		GamesList(
			state = GamesListUiState(
				list = listOf(
					GameListItem(
						id = UUID.randomUUID(),
						index = 0,
						score = 300,
					),
					GameListItem(
						id = UUID.randomUUID(),
						index = 1,
						score = 250,
					),
				),
				gameToArchive = null,
			),
			onAction = {},
		)
	}
}