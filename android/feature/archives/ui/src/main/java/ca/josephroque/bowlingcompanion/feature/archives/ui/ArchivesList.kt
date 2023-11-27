package ca.josephroque.bowlingcompanion.feature.archives.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.footer
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun ArchivesList(
	state: ArchivesListUiState,
	onAction: (ArchivesListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	state.itemToUnarchive?.let {
		AlertDialog(
			onDismissRequest = { onAction(ArchivesListUiAction.ConfirmUnarchiveClicked) },
			title = { Text(text = stringResource(R.string.unarchive_dialog_title)) },
			confirmButton = {
				TextButton(onClick = { onAction(ArchivesListUiAction.ConfirmUnarchiveClicked) }) {
					Text(text = stringResource(R.string.okay))
				}
			},
		)
	}

	LazyColumn(modifier = modifier) {
		footer(R.string.archive_list_description)

		if (state.list.isEmpty()) {
			footer(R.string.archive_list_empty)
		} else {
			archivesList(
				list = state.list,
				onItemUnarchived = { onAction(ArchivesListUiAction.UnarchiveClicked(it)) },
			)
		}
	}
}

fun LazyListScope.archivesList(
	list: List<ArchiveListItem>,
	onItemUnarchived: (ArchiveListItem) -> Unit,
) {
	items(
		items = list,
		key = { it.id },
	) { item ->
		val unarchiveAction = SwipeAction(
			icon = painterResource(R.drawable.ic_unarchive),
			background = colorResource(RCoreDesign.color.blue_600),
			onSwipe = { onItemUnarchived(item) },
		)

		SwipeableActionsBox(
			endActions = listOf(unarchiveAction),
		) {
			ArchiveItemRow(item = item)
		}
	}
}

@Composable
private fun ArchiveItemRow(
	item: ArchiveListItem,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
	) {
		Icon(
			painter = item.icon(),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurface,
		)

		Column(
			modifier = Modifier
				.padding(start = 16.dp)
				.weight(1f),
		) {
			Text(
				text = item.title(),
				style = MaterialTheme.typography.titleMedium,
			)

			Text(
				text = item.subtitle(),
				style = MaterialTheme.typography.bodyMedium,
			)

			Text(
				text = stringResource(R.string.archive_list_archived_on, item.archivedOn.toString()),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
			)
		}
	}
}

@Composable
fun ArchiveListItem.icon(): Painter = when (this) {
	is ArchiveListItem.Bowler -> painterResource(RCoreDesign.drawable.ic_person)
	is ArchiveListItem.League -> painterResource(RCoreDesign.drawable.ic_repeat)
	is ArchiveListItem.Series -> painterResource(RCoreDesign.drawable.ic_event)
	is ArchiveListItem.Game -> painterResource(RCoreDesign.drawable.ic_bowling_ball)
}

@Composable
fun ArchiveListItem.title(): String = when (this) {
	is ArchiveListItem.Bowler -> name
	is ArchiveListItem.League -> name
	is ArchiveListItem.Series -> date.toString()
	is ArchiveListItem.Game -> stringResource(R.string.archive_list_game_title, scoringMethod, score)
}

@Composable
fun ArchiveListItem.subtitle(): String = when (this) {
	is ArchiveListItem.Bowler -> stringResource(
		R.string.archive_list_bowler_description,
		numberOfLeagues,
		numberOfSeries,
		numberOfGames,
	)
	is ArchiveListItem.League -> stringResource(
		R.string.archive_list_league_description,
		bowlerName,
		numberOfSeries,
		numberOfGames,
	)
	is ArchiveListItem.Series -> stringResource(
		R.string.archive_list_series_description,
		bowlerName,
		leagueName,
		numberOfGames,
	)
	is ArchiveListItem.Game -> stringResource(
		R.string.archive_list_game_description,
		bowlerName,
		leagueName,
		seriesDate,
	)
}