package ca.josephroque.bowlingcompanion.feature.laneform.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.DiscardChangesDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.Tip
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.Stepper
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.footer
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.feature.laneslist.ui.lanesList

@Composable
fun LaneForm(
	state: LaneFormUiState,
	onAction: (LaneFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {
	if (state.isShowingDiscardChangesDialog) {
		DiscardChangesDialog(
			onDiscardChanges = { onAction(LaneFormUiAction.DiscardChangesClicked) },
			onDismiss = { onAction(LaneFormUiAction.CancelDiscardChangesClicked) },
		)
	}

	state.laneLabel?.let { dialog ->
		LaneLabelDialog(
			state = dialog,
			onAction = { onAction(LaneFormUiAction.LaneLabelDialog(it)) },
		)
	}

	state.addLanes?.let { dialog ->
		AddLanesDialog(
			state = dialog,
			onAction = { onAction(LaneFormUiAction.AddLanesDialog(it)) },
		)
	}

	LazyColumn(
		modifier = modifier.imePadding(),
		contentPadding = contentPadding,
	) {
		footer(R.string.lane_form_description)

		footer(R.string.lane_form_position_descriptions)

		if (state.lanes.isNotEmpty() && state.isShowingSwipeToEditTip) {
			item {
				Tip(
					title = R.string.lane_form_swipe_tip_title,
					message = R.string.lane_form_swipe_tip_description,
					onDismiss = { onAction(LaneFormUiAction.SwipeToEditTipDismissed) },
					modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
				)
			}
		}

		lanesList(
			state.lanes,
			onLaneClick = { onAction(LaneFormUiAction.LaneClicked(it)) },
			onLaneDelete = { onAction(LaneFormUiAction.LaneDeleted(it)) },
			onLaneEdit = { onAction(LaneFormUiAction.LaneEdited(it)) },
		)
	}
}

@Composable
private fun LaneLabelDialog(
	state: LaneLabelDialogUiState,
	onAction: (LaneLabelDialogUiAction) -> Unit,
) {
	Dialog(
		onDismissRequest = { onAction(LaneLabelDialogUiAction.CancelClicked) },
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
				LaneLabelTextField(
					label = state.label,
					onLabelChanged = { onAction(LaneLabelDialogUiAction.LabelChanged(it)) },
				)

				LaneLabelDialogMenu(
					isExpanded = state.isPositionDropDownExpanded,
					position = state.position,
					onAction = onAction,
				)

				LaneLabelDialogActions(onAction = onAction)
			}
		}
	}
}

@Composable
private fun LanePosition.text(): String = when (this) {
	LanePosition.NO_WALL -> stringResource(R.string.lane_property_position_no_wall)
	LanePosition.LEFT_WALL -> stringResource(R.string.lane_property_position_wall_on_left)
	LanePosition.RIGHT_WALL -> stringResource(R.string.lane_property_position_wall_on_right)
}

@Composable
private fun LaneLabelTextField(label: String, onLabelChanged: (String) -> Unit) {
	OutlinedTextField(
		value = label,
		onValueChange = onLabelChanged,
		label = { Text(stringResource(R.string.lane_form_property_label)) },
		singleLine = true,
		keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaneLabelDialogMenu(
	isExpanded: Boolean,
	position: LanePosition,
	onAction: (LaneLabelDialogUiAction) -> Unit,
) {
	ExposedDropdownMenuBox(
		expanded = isExpanded,
		onExpandedChange = { onAction(LaneLabelDialogUiAction.PositionDropDownToggled(it)) },
		modifier = Modifier.padding(horizontal = 16.dp),
	) {
		TextField(
			readOnly = true,
			value = position.text(),
			onValueChange = {},
			label = { Text(stringResource(R.string.lane_form_property_position)) },
			trailingIcon = {
				ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
			},
			colors = ExposedDropdownMenuDefaults.textFieldColors(),
			modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable),
		)

		ExposedDropdownMenu(
			expanded = isExpanded,
			onDismissRequest = { onAction(LaneLabelDialogUiAction.PositionDropDownToggled(false)) },
		) {
			LanePosition.entries.forEach {
				DropdownMenuItem(
					text = { Text(it.text()) },
					onClick = { onAction(LaneLabelDialogUiAction.PositionChanged(it)) },
				)
			}
		}
	}
}

@Composable
private fun LaneLabelDialogActions(onAction: (LaneLabelDialogUiAction) -> Unit) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = Modifier.fillMaxWidth(),
	) {
		Spacer(modifier = Modifier.weight(1f))

		TextButton(onClick = { onAction(LaneLabelDialogUiAction.CancelClicked) }) {
			Text(stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_cancel))
		}

		TextButton(onClick = { onAction(LaneLabelDialogUiAction.SaveClicked) }) {
			Text(stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save))
		}
	}
}

@Composable
private fun AddLanesDialog(
	state: AddLanesDialogUiState,
	onAction: (AddLanesDialogUiAction) -> Unit,
) {
	Dialog(
		onDismissRequest = { onAction(AddLanesDialogUiAction.Dismissed) },
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
				Stepper(
					title = stringResource(R.string.lane_form_add_multiple_lanes),
					value = state.lanesToAdd,
					onValueChanged = { onAction(AddLanesDialogUiAction.NumberOfLanesChanged(it)) },
				)

				AddLanesDialogActions(
					numberOfLanes = state.lanesToAdd,
					onAction = onAction,
				)
			}
		}
	}
}

@Composable
private fun AddLanesDialogActions(numberOfLanes: Int, onAction: (AddLanesDialogUiAction) -> Unit) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = Modifier.fillMaxWidth(),
	) {
		Spacer(modifier = Modifier.weight(1f))

		TextButton(onClick = { onAction(AddLanesDialogUiAction.Dismissed) }) {
			Text(stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_cancel))
		}

		TextButton(onClick = { onAction(AddLanesDialogUiAction.AddLanesClicked(numberOfLanes)) }) {
			Text(stringResource(R.string.lane_form_add_x_lanes, numberOfLanes))
		}
	}
}
