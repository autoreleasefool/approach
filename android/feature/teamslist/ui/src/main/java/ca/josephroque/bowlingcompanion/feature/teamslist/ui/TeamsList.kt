package ca.josephroque.bowlingcompanion.feature.teamslist.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.DeleteDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.model.TeamListItem
import ca.josephroque.bowlingcompanion.core.model.ui.TeamRow
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun TeamsList(
	state: TeamsListUiState,
	onAction: (TeamsListUiAction) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
	header: (@Composable LazyItemScope.() -> Unit)? = null,
) {
	state.teamToDelete?.let {
		DeleteDialog(
			itemName = it.name,
			onDelete = { onAction(TeamsListUiAction.ConfirmDeleteClicked) },
			onDismiss = { onAction(TeamsListUiAction.DismissDeleteClicked) },
		)
	}

	LazyColumn(
		modifier = modifier,
		contentPadding = contentPadding,
	) {
		if (state.list.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.team_list_empty_title,
					icon = R.drawable.team_list_empty_state,
					message = R.string.team_list_empty_message,
					action = R.string.team_list_add,
					onActionClick = { onAction(TeamsListUiAction.AddTeamClicked) },
				)
			}
		} else {
			header?.also {
				item {
					it()
				}
			}

			teamsList(
				list = state.list,
				onTeamClick = { onAction(TeamsListUiAction.TeamClicked(it)) },
				onEditTeam = { onAction(TeamsListUiAction.TeamEdited(it)) },
				onDeleteTeam = { onAction(TeamsListUiAction.TeamDeleted(it)) },
			)
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.teamsList(
	list: List<TeamListItem>,
	onTeamClick: (TeamListItem) -> Unit,
	onEditTeam: (TeamListItem) -> Unit,
	onDeleteTeam: (TeamListItem) -> Unit,
) {
	items(
		items = list,
		key = { it.id },
	) {
		val deleteAction = SwipeAction(
			icon = rememberVectorPainter(Icons.Filled.Delete),
			background = colorResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
			),
			onSwipe = { onDeleteTeam(it) },
		)

		val editAction = SwipeAction(
			icon = rememberVectorPainter(Icons.Default.Edit),
			background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.blue_300),
			onSwipe = { onEditTeam(it) },
		)
		
		SwipeableActionsBox(
			startActions = listOf(deleteAction),
			endActions = listOf(editAction),
			modifier = Modifier.animateItemPlacement(),
		) {
			TeamRow(
				name = it.name,
				members = it.bowlers.split(";").sorted(),
				onClick = { onTeamClick(it) },
			)
		}
	}
}
