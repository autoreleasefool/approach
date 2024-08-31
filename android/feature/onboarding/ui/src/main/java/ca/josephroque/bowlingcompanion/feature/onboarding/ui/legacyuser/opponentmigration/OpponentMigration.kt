package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.CheckBoxRow
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R

@Composable
fun OpponentMigration(
	state: OpponentMigrationUiState,
	onAction: (OpponentMigrationUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (state.dialog != null) {
		OpponentMigrationDialog(
			state = state.dialog,
			onAction = onAction,
		)
	}

	val listState = rememberLazyListState()
	LaunchedEffect(state.isMigrating) {
		if (state.isMigrating) {
			listState.animateScrollToItem(0)
		}
	}

	LazyColumn(
		state = listState,
		modifier = modifier,
	) {
		if (!state.isMigrating) {
			item {
				InstructionsCard()
			}
		} else {
			header(R.string.onboarding_opponents_list_title)

			items(
				items = state.list,
				key = { it.id },
			) { opponent ->
				CheckBoxRow(
					isSelected = state.selected.contains(opponent.id),
					onClick = {
						onAction(OpponentMigrationUiAction.OpponentClicked(opponent))
					},
					content = {
						Column {
							BowlerRow(
								name = opponent.name,
								kind = opponent.kind,
								iconSpacing = 8.dp,
							)

							if (opponent.mergedBowlerNames.isNotEmpty()) {
								Text(
									text = stringResource(
										R.string.onboarding_opponents_merged_with,
										opponent.mergedBowlerNames.joinToString(", "),
									),
									style = MaterialTheme.typography.bodySmall,
									modifier = Modifier.padding(start = 32.dp),
								)
							}
						}
					},
				)
			}
		}
	}
}

@Composable
private fun InstructionsCard() {
	Column(
		verticalArrangement = Arrangement.spacedBy(8.dp),
		modifier = Modifier
			.padding(horizontal = 16.dp)
			.padding(bottom = 8.dp),
	) {
		Card(
			modifier = Modifier.fillMaxWidth(),
		) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
			) {
				Icon(
					Icons.Default.Warning,
					contentDescription = null,
					tint = colorResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.color.warning_container,
					),
				)

				Text(
					text = stringResource(R.string.onboarding_opponents_action_required),
					style = MaterialTheme.typography.titleLarge,
				)
			}
		}

		listOf(
			R.string.onboarding_opponents_description_1_previous_version,
			R.string.onboarding_opponents_description_2_current_version,
			R.string.onboarding_opponents_description_3_opponents_created,
			R.string.onboarding_opponents_description_4_migration_required,
			R.string.onboarding_opponents_description_5_when_done,
		).forEach {
			Text(
				text = stringResource(it),
				style = MaterialTheme.typography.bodyMedium,
			)
		}
	}
}

@Composable
private fun OpponentMigrationDialog(
	state: OpponentMigrationDialogUiState,
	onAction: (OpponentMigrationUiAction) -> Unit,
) {
	when (state) {
		is OpponentMigrationDialogUiState.NameDialog -> NameDialog(
			state = state,
			onAction = { onAction(OpponentMigrationUiAction.OpponentNameDialog(it)) },
		)
		is OpponentMigrationDialogUiState.TooManyBowlersDialog -> TooManyBowlersDialog(
			state = state,
			onAction = { onAction(OpponentMigrationUiAction.TooManyBowlersDialog(it)) },
		)
	}
}

@Composable
private fun TooManyBowlersDialog(
	state: OpponentMigrationDialogUiState.TooManyBowlersDialog,
	onAction: (TooManyBowlersDialogUiAction) -> Unit,
) {
	AlertDialog(
		onDismissRequest = { onAction(TooManyBowlersDialogUiAction.Dismissed) },
		title = {
			Text(text = stringResource(R.string.onboarding_opponents_too_many_bowlers_title))
		},
		text = {
			Text(
				text = stringResource(
					R.string.onboarding_opponents_too_many_bowlers_message,
					state.firstName,
					state.secondName,
				),
			)
		},
		confirmButton = {
			TextButton(onClick = { onAction(TooManyBowlersDialogUiAction.Dismissed) }) {
				Text(
					text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_ok),
				)
			}
		},
	)
}

@Composable
private fun NameDialog(
	state: OpponentMigrationDialogUiState.NameDialog,
	onAction: (OpponentMigrationNameDialogUiAction) -> Unit,
) {
	Dialog(
		onDismissRequest = { onAction(OpponentMigrationNameDialogUiAction.Dismissed) },
	) {
		Surface(
			shape = MaterialTheme.shapes.medium,
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
			) {
				Text(
					text = stringResource(R.string.onboarding_opponents_choose_a_name),
					style = MaterialTheme.typography.titleMedium,
				)
				
				OutlinedTextField(
					value = state.name,
					onValueChange = { onAction(OpponentMigrationNameDialogUiAction.NameChanged(it)) },
					label = {
						Text(text = stringResource(R.string.onboarding_opponents_name))
					},
					singleLine = true,
					modifier = Modifier.fillMaxWidth(),
				)

				NameDialogActions(
					isSaveEnabled = state.name.isNotBlank(),
					onAction = onAction,
				)
			}
		}
	}
}

@Composable
private fun NameDialogActions(
	isSaveEnabled: Boolean,
	onAction: (OpponentMigrationNameDialogUiAction) -> Unit,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = Modifier.fillMaxWidth(),
	) {
		Spacer(modifier = Modifier.weight(1f))

		TextButton(onClick = { onAction(OpponentMigrationNameDialogUiAction.Dismissed) }) {
			Text(stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_cancel))
		}

		TextButton(
			onClick = { onAction(OpponentMigrationNameDialogUiAction.Confirmed) },
			enabled = isSaveEnabled,
		) {
			Text(stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save))
		}
	}
}

@Composable
@Preview
private fun OpponentMigrationPreview() {
	OpponentMigration(
		state = OpponentMigrationUiState(
			list = listOf(
				MergedBowler(
					id = BowlerID.randomID(),
					name = "John Doe",
					mergedBowlerNames = listOf("John Doe", "Jane Doe"),
					kind = BowlerKind.PLAYABLE,
				),
				MergedBowler(
					id = BowlerID.randomID(),
					name = "Jane Doe",
					mergedBowlerNames = listOf("John Doe", "Jane Doe"),
					kind = BowlerKind.PLAYABLE,
				),
			),
			selected = emptySet(),
			isMigrating = true,
		),
		onAction = {},
	)
}
