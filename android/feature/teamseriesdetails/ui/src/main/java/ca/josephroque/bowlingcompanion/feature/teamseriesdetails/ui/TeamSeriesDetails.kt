package ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.charts.ui.SeriesHeader
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.datetime.LocalDate
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun TeamSeriesDetails(
	state: TeamSeriesDetailsUiState,
	onAction: (TeamSeriesDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	state.gameToArchive?.let {
		ArchiveDialog(
			itemName = stringResource(R.string.team_series_bowlers_game, it.gameIndex + 1, it.bowlerName),
			onArchive = { onAction(TeamSeriesDetailsUiAction.ConfirmArchiveClicked) },
			onDismiss = { onAction(TeamSeriesDetailsUiAction.DismissArchiveClicked) },
		)
	}

	state.gameToRestore?.let {
		AlertDialog(
			onDismissRequest = { onAction(TeamSeriesDetailsUiAction.ConfirmRestoreClicked) },
			title = { Text(text = stringResource(R.string.team_series_game_restored)) },
			confirmButton = {
				TextButton(onClick = { onAction(TeamSeriesDetailsUiAction.ConfirmRestoreClicked) }) {
					Text(
						text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_ok),
					)
				}
			},
		)
	}

	LazyColumn(modifier = modifier) {
		item {
			Column {
				Text(
					text = stringResource(R.string.team_series_team_title, state.teamSeries.teamName),
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 8.dp),
				)

				SeriesHeader(
					preBowl = null,
					preBowledDate = null,
					numberOfGames = state.teamSeries.numberOfGames,
					seriesTotal = state.teamSeries.total,
					seriesLow = state.teamSeries.seriesLow,
					seriesHigh = state.teamSeries.seriesHigh,
					isShowingPlaceholder = state.isShowingPlaceholder,
					scores = state.teamSeries.teamScores,
					modifier = Modifier.fillMaxWidth(),
				)
			}
		}

		items(
			items = state.listItems,
			key = {
				when (it) {
					is TeamSeriesDetailsUiState.ListItem.GameHeader -> "${it.gameIndex}-header"
					is TeamSeriesDetailsUiState.ListItem.GameRow -> "${it.gameId}"
				}
			},
			contentType = {
				when (it) {
					is TeamSeriesDetailsUiState.ListItem.GameHeader -> "Header"
					is TeamSeriesDetailsUiState.ListItem.GameRow -> "Row"
				}
			},
		) {
			when (it) {
				is TeamSeriesDetailsUiState.ListItem.GameHeader -> GameHeader(
					gameIndex = it.gameIndex,
					teamTotal = it.teamTotal,
				)

				is TeamSeriesDetailsUiState.ListItem.GameRow -> GameRow(
					bowlerName = it.bowlerName,
					score = it.score,
					isArchived = it.isArchived,
					onClick = { onAction(TeamSeriesDetailsUiAction.GameClicked(it.gameId)) },
					onArchive = { onAction(TeamSeriesDetailsUiAction.GameArchived(it.gameId)) },
					onRestore = { onAction(TeamSeriesDetailsUiAction.GameRestored(it.gameId)) },
				)
			}
		}
	}
}

@Composable
private fun GameHeader(gameIndex: Int, teamTotal: Int) {
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.surfaceContainer)
			.padding(16.dp),
	) {
		Text(
			text = stringResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal,
				gameIndex + 1,
			),
			style = MaterialTheme.typography.titleMedium,
		)

		Text(
			text = "$teamTotal",
			style = MaterialTheme.typography.bodyLarge,
		)
	}
}

@Composable
private fun GameRow(
	bowlerName: String,
	score: Int,
	isArchived: Boolean,
	onClick: () -> Unit,
	onArchive: () -> Unit,
	onRestore: () -> Unit,
) {
	val swipeAction = if (isArchived) {
		SwipeAction(
			icon = painterResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_unarchive,
			),
			background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.blue_600),
			onSwipe = onRestore,
		)
	} else {
		SwipeAction(
			icon = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_archive),
			background = colorResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
			),
			onSwipe = onArchive,
		)
	}

	SwipeableActionsBox(
		startActions = listOf(swipeAction),
	) {
		val textAlpha = if (isArchived) 0.6f else 1f

		Column(
			verticalArrangement = Arrangement.spacedBy(4.dp),
			modifier = Modifier
				.clickable(onClick = onClick)
				.padding(16.dp),
		) {
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(
					text = bowlerName,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha),
				)

				Text(
					text = "$score",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha),
				)
			}

			if (isArchived) {
				Text(
					text = stringResource(R.string.team_series_game_archived),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha),
					fontStyle = FontStyle.Italic,
				)
			}
		}
	}
}

@Preview
@Composable
private fun TeamSeriesDetailsPreview() {
	Surface {
		TeamSeriesDetails(
			state = TeamSeriesDetailsUiState(
				teamSeries = TeamSeriesDetailsUiState.TeamSeries(
					teamName = "Besties",
					date = LocalDate.parse("2024-09-08"),
					total = 1234,
					seriesHigh = 210,
					seriesLow = 190,
					numberOfGames = 4,
					teamScores = ChartEntryModelProducer(
						listOf(
							entryOf(0, 200),
							entryOf(1, 210),
							entryOf(2, 190),
							entryOf(3, 200),
						),
					),
				),
				listItems = listOf(
					TeamSeriesDetailsUiState.ListItem.GameHeader(
						gameIndex = 0,
						teamTotal = 1234,
					),
					TeamSeriesDetailsUiState.ListItem.GameRow(
						gameId = GameID.randomID(),
						bowlerName = "Joseph",
						score = 456,
						isArchived = false,
					),
					TeamSeriesDetailsUiState.ListItem.GameRow(
						gameId = GameID.randomID(),
						bowlerName = "Sarah",
						score = 678,
						isArchived = false,
					),
					TeamSeriesDetailsUiState.ListItem.GameHeader(
						gameIndex = 1,
						teamTotal = 1234,
					),
					TeamSeriesDetailsUiState.ListItem.GameRow(
						gameId = GameID.randomID(),
						bowlerName = "Joseph",
						score = 456,
						isArchived = false,
					),
					TeamSeriesDetailsUiState.ListItem.GameRow(
						gameId = GameID.randomID(),
						bowlerName = "Sarah",
						score = 678,
						isArchived = true,
					),
				),
				isShowingPlaceholder = false,
				gameToArchive = null,
				gameToRestore = null,
			),
			onAction = {},
		)
	}
}
