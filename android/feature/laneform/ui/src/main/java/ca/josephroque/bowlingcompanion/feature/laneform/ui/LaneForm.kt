package ca.josephroque.bowlingcompanion.feature.laneform.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.Stepper
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.feature.laneslist.ui.lanesList

@Composable
fun LaneForm(
	state: LaneFormUiState,
	onAction: (LaneFormUiAction) -> Unit,
) {
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
		modifier = Modifier.fillMaxSize(),
	) {
		lanesList(
			state.lanes,
			onLaneClick = { LaneFormUiAction.LaneClicked(it) },
			onLaneDelete = { LaneFormUiAction.LaneDeleted(it) },
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaneLabelDialog(
	state: LaneLabelDialogUiState,
	onAction: (LaneLabelDialogUiAction) -> Unit,
) {
	AlertDialog(
		onDismissRequest = { onAction(LaneLabelDialogUiAction.DiscardClicked) },
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier.fillMaxSize(),
		) {
			OutlinedTextField(
				value = state.label,
				onValueChange = { onAction(LaneLabelDialogUiAction.LabelChanged(it)) },
				label = { Text(stringResource(R.string.lane_form_property_label)) },
				singleLine = true,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp),
			)

			ExposedDropdownMenuBox(
				expanded = state.isPositionDropDownExpanded,
				onExpandedChange = { onAction(LaneLabelDialogUiAction.PositionDropDownToggled(it)) },
			) {
				TextField(
					readOnly = true,
					value = state.position.text(),
					onValueChange = {},
					label = { Text(stringResource(R.string.lane_form_property_position)) },
					trailingIcon = {
						ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.isPositionDropDownExpanded)
					},
					colors = ExposedDropdownMenuDefaults.textFieldColors(),
					modifier = Modifier.menuAnchor(),
				)

				ExposedDropdownMenu(
					expanded = state.isPositionDropDownExpanded,
					onDismissRequest = { onAction(LaneLabelDialogUiAction.PositionDropDownToggled(false)) },
					) {
					LanePosition.entries.forEach {
						DropdownMenuItem(
							text = { Text(it.text()) },
							onClick = { onAction(LaneLabelDialogUiAction.PositionChanged(it)) }
						)
					}
				}
			}

			Button(onClick = { onAction(LaneLabelDialogUiAction.SaveClicked) }) {
				Text(stringResource(RCoreDesign.string.action_save))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddLanesDialog(
	state: AddLanesDialogUiState,
	onAction: (AddLanesDialogUiAction) -> Unit,
) {
	AlertDialog(
		onDismissRequest = { onAction(AddLanesDialogUiAction.Dismissed) },
	) {
		Stepper(
			title = stringResource(R.string.lane_form_add_x_lanes, state.lanesToAdd),
			value = state.lanesToAdd,
			onValueChanged = { onAction(AddLanesDialogUiAction.NumberOfLanesChanged(it)) },
		)

		Button(onClick = { onAction(AddLanesDialogUiAction.AddLanesClicked(state.lanesToAdd)) }) {
			Text(stringResource(RCoreDesign.string.action_save))
		}
	}
}