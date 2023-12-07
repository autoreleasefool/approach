package ca.josephroque.bowlingcompanion.feature.leagueslist.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.ui.LeagueRow
import kotlinx.datetime.LocalDate
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.util.UUID

@Composable
fun LeaguesList(
	state: LeaguesListUiState,
	onAction: (LeaguesListUiAction) -> Unit,
	modifier: Modifier = Modifier,
	header: (@Composable LazyItemScope.() -> Unit)? = null,
) {
	state.leagueToArchive?.let {
		ArchiveDialog(
			itemName = it.name,
			onArchive = { onAction(LeaguesListUiAction.ConfirmArchiveClicked) },
			onDismiss = { onAction(LeaguesListUiAction.DismissArchiveClicked) },
		)
	}

	LazyColumn(modifier = modifier) {
		if (state.list.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.league_list_empty_title,
					icon = R.drawable.league_list_empty_state,
					message = R.string.league_list_empty_message,
					action = R.string.league_list_add,
					onActionClick = { onAction(LeaguesListUiAction.AddLeagueClicked) },
				)
			}
		} else {
			header?.also {
				item {
					it()
				}
			}

			leaguesList(
				list = state.list,
				onLeagueClick = { onAction(LeaguesListUiAction.LeagueClicked(it.id)) },
				onArchiveLeague = { onAction(LeaguesListUiAction.LeagueArchived(it)) },
				onEditLeague = { onAction(LeaguesListUiAction.LeagueEdited(it.id)) },
			)
		}
	}
}

fun LazyListScope.leaguesList(
	list: List<LeagueListItem>,
	onLeagueClick: (LeagueListItem) -> Unit,
	onArchiveLeague: (LeagueListItem) -> Unit,
	onEditLeague: (LeagueListItem) -> Unit,
) {
	items(
		items = list,
		key = { it.id },
	) {
		val archiveAction = SwipeAction(
			icon = painterResource(RCoreDesign.drawable.ic_archive),
			background = colorResource(RCoreDesign.color.destructive),
			onSwipe = { onArchiveLeague(it) },
		)

		val editAction = SwipeAction(
			icon = rememberVectorPainter(Icons.Default.Edit),
			background = colorResource(RCoreDesign.color.blue_300),
			onSwipe = { onEditLeague(it) },
		)

		SwipeableActionsBox(
			startActions = listOf(archiveAction),
			endActions = listOf(editAction),
		) {
			LeagueRow(
				name = it.name,
				recurrence = it.recurrence,
				lastSeriesDate = it.lastSeriesDate,
				average = it.average,
				onClick = { onLeagueClick(it) },
			)
		}
	}
}

@Preview
@Composable
fun LeaguesListPreview() {
	Surface {
		LeaguesList(
			state = LeaguesListUiState(
				leagueToArchive = null,
				list = listOf(
					LeagueListItem(
						id = UUID.randomUUID(),
						name = "Majors 23/24",
						average = 200.0,
						recurrence = LeagueRecurrence.REPEATING,
						lastSeriesDate = LocalDate(2023, 1, 1),
					),
					LeagueListItem(
						id = UUID.randomUUID(),
						name = "Minors 22/23",
						average = 150.0,
						recurrence = LeagueRecurrence.ONCE,
						lastSeriesDate = LocalDate(2022, 1, 1),
					),
				),
			),
			onAction = {},
		)
	}
}