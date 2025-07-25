package ca.josephroque.bowlingcompanion.feature.quickplay.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormRadioGroup
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.Stepper
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.ListSectionFooter
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.stub.BowlerSummaryStub
import ca.josephroque.bowlingcompanion.core.model.stub.LeagueSummaryStub
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun QuickPlay(state: QuickPlayUiState, onAction: (QuickPlayUiAction) -> Unit, modifier: Modifier = Modifier) {
	val hapticFeedback = LocalHapticFeedback.current

	val lazyListState = rememberLazyListState()
	val reorderableLazyListState = rememberReorderableLazyListState(
		lazyListState = lazyListState,
		onMove = { from, to ->
			onAction(QuickPlayUiAction.BowlerMoved(from.index, to.index, 1))

			hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
		},
	)

	LazyColumn(
		state = lazyListState,
		modifier = modifier.padding(bottom = 8.dp),
	) {
		item {
			if (state.isShowingQuickPlayTip) {
				QuickPlayTip(
					onClick = { onAction(QuickPlayUiAction.TipClicked) },
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 8.dp),
				)
			}
			
			if (state.isShowingLeagueRecurrencePicker) {
				RecurrencePicker(
					recurrence = state.leagueRecurrence,
					onRecurrenceChanged = { onAction(QuickPlayUiAction.LeagueRecurrenceChanged(it)) },
				)

				when (state.leagueRecurrence) {
					LeagueRecurrence.REPEATING -> Unit
					LeagueRecurrence.ONCE -> LeagueNameField(
						name = state.leagueName,
						onNameChanged = { onAction(QuickPlayUiAction.LeagueNameChanged(it)) },
						errorId = state.leagueNameErrorId,
					)
				}

				HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
			}
		}

		// If number of items before reorderable list changes,
		// reorderableLazyListState#onMove must be updated
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
				state = reorderableLazyListState,
				key = bowler.first.id,
			) { isDragging ->
				val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

				SwipeableActionsBox(
					startActions = listOfNotNull(deleteAction),
				) {
					Surface(shadowElevation = elevation) {
						QuickPlayBowler(
							bowler = bowler,
							onAction = onAction,
						)
					}
				}
			}
		}

		item {
			ListSectionFooter(footerResourceId = R.string.quick_play_long_press_to_reorder)

			HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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
				enabled = state.isStartButtonEnabled,
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
private fun ReorderableCollectionItemScope.QuickPlayBowler(
	bowler: Pair<BowlerSummary, LeagueSummary?>,
	onAction: (QuickPlayUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val hapticFeedback = LocalHapticFeedback.current

	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
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
			Text(
				text = leagueName ?: stringResource(R.string.quick_play_no_league_selected),
				style = MaterialTheme.typography.bodyMedium,
				color = if (leagueName == null) {
					MaterialTheme.colorScheme.error
				} else {
					Color.Unspecified
				},
			)
		}

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
				tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
			)
		}
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

@Composable
private fun LeagueNameField(name: String, onNameChanged: (String) -> Unit, errorId: Int?) {
	OutlinedTextField(
		value = name,
		onValueChange = onNameChanged,
		label = { Text(stringResource(R.string.team_play_league_recurrence_league_name)) },
		singleLine = true,
		isError = errorId != null,
		keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
		supportingText = {
			errorId?.let {
				Text(
					text = stringResource(it),
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.fillMaxWidth(),
				)
			}
		},
		trailingIcon = {
			if (errorId != null) {
				Icon(
					Icons.Default.Warning,
					tint = MaterialTheme.colorScheme.error,
					contentDescription = null,
				)
			}
		},
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
			.padding(top = 8.dp),
	)
}

@Composable
private fun RecurrencePicker(recurrence: LeagueRecurrence, onRecurrenceChanged: (LeagueRecurrence) -> Unit) {
	FormRadioGroup(
		title = stringResource(R.string.team_play_league_recurrence),
		subtitle = stringResource(R.string.team_play_league_recurrence_subtitle),
		options = LeagueRecurrence.entries.toTypedArray(),
		selected = recurrence,
		titleForOption = {
			when (it) {
				LeagueRecurrence.REPEATING -> stringResource(R.string.team_play_league_recurrence_league)
				LeagueRecurrence.ONCE -> stringResource(R.string.team_play_league_recurrence_one_time_event)
				null -> ""
			}
		},
		onOptionSelected = {
			it ?: return@FormRadioGroup
			onRecurrenceChanged(it)
		},
	)
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
