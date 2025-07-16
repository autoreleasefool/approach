package ca.josephroque.bowlingcompanion.feature.gameslist.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.EmptyStateAction
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.ReorderableGameListItem
import ca.josephroque.bowlingcompanion.core.model.ui.GameRow
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun GamesList(
	state: GamesListUiState,
	onAction: (GamesListUiAction) -> Unit,
	modifier: Modifier = Modifier,
	header: (@Composable LazyItemScope.() -> Unit)? = null,
) {
	val hapticFeedback = LocalHapticFeedback.current

	val lazyListState = rememberLazyListState()
	val reorderableLazyListState = rememberReorderableLazyListState(
		lazyListState = lazyListState,
		onMove = { from, to ->
			if (!state.isReordering) return@rememberReorderableLazyListState
			onAction(GamesListUiAction.GameMoved(
				from = from.index,
				to = to.index,
				offset = if (header == null) 0 else 1
			))

			hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
		}
	)

	val itemsList = remember(state.list, state.isReordering) {
		state.list.map { ReorderableGameListItem(it, state.isReordering) }
	}

	state.gameToArchive?.let {
		ArchiveDialog(
			itemName = stringResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal,
				it.index + 1,
			),
			onArchive = { onAction(GamesListUiAction.ConfirmArchiveClicked) },
			onDismiss = { onAction(GamesListUiAction.DismissArchiveClicked) },
		)
	}

	LazyColumn(
		state = lazyListState,
		modifier = modifier,
	) {
		if (itemsList.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.game_list_empty_title,
					icon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.not_found,
					message = R.string.game_list_empty_message,
					action = EmptyStateAction(
						title = R.string.game_list_add,
						onClick = { onAction(GamesListUiAction.AddGameClicked) },
					),
				)
			}
		} else {
			header?.also {
				item {
					it()
				}
			}

			gamesList(
				list = itemsList,
				reorderableLazyListState = reorderableLazyListState,
				onGameClick = { onAction(GamesListUiAction.GameClicked(it.id)) },
				onArchiveGame = { onAction(GamesListUiAction.GameArchived(it)) },
			)
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.gamesList(
	list: List<ReorderableGameListItem>,
	reorderableLazyListState: ReorderableLazyListState,
	onGameClick: (GameListItem) -> Unit,
	onArchiveGame: (GameListItem) -> Unit,
) {

	items(
		items = list,
		key = { it.id },
	) {
		val hapticFeedback = LocalHapticFeedback.current

		val archiveAction = SwipeAction(
			icon = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_archive),
			background = colorResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
			),
			onSwipe = { onArchiveGame(it.toGameListItem()) },
		)

		val isDragHandleVisible = remember { mutableStateOf(false) }
		LaunchedEffect(it.isReordering) {
			isDragHandleVisible.value = it.isReordering
		}

		ReorderableItem(
			state = reorderableLazyListState,
			key = it.id
		) { isDragging ->
			val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

			SwipeableActionsBox(
				startActions = if (it.isReordering) emptyList() else listOf(archiveAction),
				modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
			) {
				Surface(shadowElevation = elevation) {
					Row(
						horizontalArrangement = Arrangement.spacedBy(16.dp),
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier
							.fillMaxWidth()
							.padding(if (it.isReordering) 16.dp else 0.dp)
					) {
						GameRow(
							index = it.index,
							score = it.score,
							onClick = if (it.isReordering) null else { { onGameClick(it.toGameListItem()) } },
							modifier = if (it.isReordering) Modifier.weight(1f) else Modifier.fillMaxWidth(),
						)

						AnimatedVisibility(visible = isDragHandleVisible.value) {
							IconButton(
								modifier = Modifier.draggableHandle(
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
									painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_drag_handle),
									contentDescription = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.cd_reorder),
									tint = MaterialTheme.colorScheme.onSurface,
								)
							}
						}
					}
				}
			}
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
						id = GameID.randomID(),
						index = 0,
						score = 300,
					),
					GameListItem(
						id = GameID.randomID(),
						index = 1,
						score = 250,
					),
				),
				gameToArchive = null,
				isReordering = false,
			),
			onAction = {},
		)
	}
}
