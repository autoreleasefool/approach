package ca.josephroque.bowlingcompanion.feature.quickplay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.Stepper
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.stub.BowlerSummaryStub
import ca.josephroque.bowlingcompanion.core.model.stub.LeagueSummaryStub
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun QuickPlay(
	state: QuickPlayUiState,
	onAction: (QuickPlayUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val reorderableState = rememberReorderableLazyListState(
		onMove = { from, to ->
			onAction(QuickPlayUiAction.BowlerMoved(from.index, to.index))
		},
	)

	LazyColumn(
		state = reorderableState.listState,
		modifier = modifier
			.reorderable(reorderableState)
			.detectReorderAfterLongPress(reorderableState)
			.padding(bottom = 8.dp),
	) {
		if (state.isShowingQuickPlayTip) {
			item {
				QuickPlayTip(
					onClick = { onAction(QuickPlayUiAction.TipClicked) },
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 8.dp),
				)
			}
		}

		// If number of items before reorderable list changes,
		// QuickPlayViewModel#moveBowler must be updated
		items(
			state.bowlers,
			key = { it.first.id },
		) { bowler ->
			val deleteAction = if (state.bowlers.size > 1 && state.isDeleteBowlersEnabled) {
				SwipeAction(
					icon = rememberVectorPainter(Icons.Filled.Delete),
					background = colorResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
					),
					onSwipe = { onAction(QuickPlayUiAction.BowlerDeleted(bowler.first)) },
				)
			} else {
				null
			}

			ReorderableItem(
				reorderableState = reorderableState,
				key = bowler.first.id,
			) { _ ->
				SwipeableActionsBox(
					startActions = listOfNotNull(deleteAction),
				) {
					QuickPlayBowler(
						bowler = bowler,
						onAction = onAction,
					)
				}
			}
		}

		item {
			Stepper(
				title = stringResource(R.string.quick_play_number_of_games),
				value = state.numberOfGames,
				onValueChanged = { onAction(QuickPlayUiAction.NumberOfGamesChanged(it)) },
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 8.dp),
			)
		}

		item {
			Button(
				onClick = { onAction(QuickPlayUiAction.StartClicked) },
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp),
			) {
				Text(
					text = stringResource(R.string.quick_play_get_started),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}
	}
}

@Composable
private fun QuickPlayBowler(
	bowler: Pair<BowlerSummary, LeagueSummary?>,
	onAction: (QuickPlayUiAction) -> Unit,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.background(MaterialTheme.colorScheme.surface)
			.fillMaxWidth()
			.clickable(onClick = { onAction(QuickPlayUiAction.BowlerClicked(bowler.first)) })
			.padding(horizontal = 16.dp, vertical = 8.dp),
	) {
		Icon(
			Icons.Default.Person,
			contentDescription = null,
		)

		Column(
			horizontalAlignment = Alignment.Start,
			verticalArrangement = Arrangement.spacedBy(2.dp),
			modifier = Modifier.weight(1f),
		) {
			Text(
				text = bowler.first.name,
				style = MaterialTheme.typography.bodyLarge,
			)

			val leagueName = bowler.second?.name
			if (leagueName != null) {
				Text(
					text = leagueName,
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}

		Icon(
			Icons.Default.Menu,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
		)
	}
}

@Composable
private fun QuickPlayTip(onClick: () -> Unit, modifier: Modifier = Modifier) {
	Card(
		onClick = onClick,
		modifier = modifier,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			Icon(
				Icons.Default.Info,
				contentDescription = null,
			)

			Text(
				text = stringResource(R.string.quick_play_learn_how_to_use),
				style = MaterialTheme.typography.bodyMedium,
			)
		}
	}
}

@Preview
@Composable
private fun QuickPlayPreview() {
	Surface {
		QuickPlay(
			state = QuickPlayUiState(
				bowlers = BowlerSummaryStub.list().zip(LeagueSummaryStub.list()),
				isShowingQuickPlayTip = true,
			),
			onAction = {},
		)
	}
}
