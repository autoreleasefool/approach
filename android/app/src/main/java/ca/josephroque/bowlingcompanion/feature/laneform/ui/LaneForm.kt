package ca.josephroque.bowlingcompanion.feature.laneform.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.Stepper
import ca.josephroque.bowlingcompanion.core.database.model.asLaneListItem
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.feature.laneform.LaneFormUiState
import ca.josephroque.bowlingcompanion.feature.laneslist.LaneItemRow
import java.util.UUID

@Composable
internal fun LaneForm(
	laneFormState: LaneFormUiState,
	onAddLanes: (Int) -> Unit,
	dismissAddLanesDialog: () -> Unit,
	onLanesToAddChanged: (Int) -> Unit,
	finishLaneEdits: (Boolean) -> Unit,
	showLaneLabelDialog: (UUID) -> Unit,
	onLabelChanged: (String) -> Unit,
	onPositionChanged: (LanePosition) -> Unit,
	onTogglePositionDropDown: (Boolean) -> Unit,
	onDeleteLane: (UUID) -> Unit,
) {
	LaneLabelDialog(
		laneFormState = laneFormState,
		finishLaneEdits = finishLaneEdits,
		onLabelChanged = onLabelChanged,
		onPositionChanged = onPositionChanged,
		onTogglePositionDropDown = onTogglePositionDropDown,
	)

	AddLanesDialog(
		laneFormState = laneFormState,
		onAddLanes = onAddLanes,
		onLanesToAddChanged = onLanesToAddChanged,
		dismissAddLanesDialog = dismissAddLanesDialog,
	)

	LazyColumn(
		modifier = Modifier.fillMaxSize(),
	) {
		when (laneFormState) {
			LaneFormUiState.Dismissed, LaneFormUiState.Loading -> Unit
			is LaneFormUiState.Success ->
				items(
					laneFormState.lanes,
					key = { it.id },
				) {
					LaneItemRow(
						lane = it.asLaneListItem(),
						onClick = { showLaneLabelDialog(it.id) },
					)
				}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaneLabelDialog(
	laneFormState: LaneFormUiState,
	onTogglePositionDropDown: (Boolean) -> Unit,
	onLabelChanged: (String) -> Unit,
	onPositionChanged: (LanePosition) -> Unit,
	finishLaneEdits: (Boolean) -> Unit,
) {
	when (laneFormState) {
		LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
		is LaneFormUiState.Success -> laneFormState.laneLabelDialogState?.let {
			AlertDialog(
				onDismissRequest = { finishLaneEdits(false) },
			) {
				Column(
					verticalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier.fillMaxSize(),
				) {
					OutlinedTextField(
						value = it.label,
						onValueChange = onLabelChanged,
						label = { Text(stringResource(R.string.lane_form_property_label)) },
						singleLine = true,
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 16.dp),
					)

					ExposedDropdownMenuBox(
						expanded = it.isPositionDropDownExpanded,
						onExpandedChange = onTogglePositionDropDown,
					) {
						TextField(
							readOnly = true,
							value = it.position.text(),
							onValueChange = {},
							label = { Text(stringResource(R.string.lane_form_property_position)) },
							trailingIcon = {
								ExposedDropdownMenuDefaults.TrailingIcon(expanded = it.isPositionDropDownExpanded)
							},
							colors = ExposedDropdownMenuDefaults.textFieldColors(),
							modifier = Modifier.menuAnchor(),
						)

						ExposedDropdownMenu(
							expanded = it.isPositionDropDownExpanded,
							onDismissRequest = { onTogglePositionDropDown(false) },
							) {
							LanePosition.values().forEach {
								DropdownMenuItem(
									text = { Text(it.text()) },
									onClick = {
										onPositionChanged(it)
									}
								)
							}
						}
					}

					Button(onClick = { finishLaneEdits(true) }) {
						Text(stringResource(RCoreDesign.string.action_save))
					}
				}
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
	laneFormState: LaneFormUiState,
	onAddLanes: (Int) -> Unit,
	onLanesToAddChanged: (Int) -> Unit,
	dismissAddLanesDialog: () -> Unit,
) {
	when (laneFormState) {
		LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
		is LaneFormUiState.Success -> laneFormState.addLanesDialogState?.let {
			AlertDialog(
				onDismissRequest = dismissAddLanesDialog,
			) {
				Stepper(
					title = stringResource(R.string.lane_form_add_x_lanes, it.lanesToAdd),
					value = it.lanesToAdd,
					onValueChanged = onLanesToAddChanged,
				)

				Button(onClick = { onAddLanes(it.lanesToAdd) }) {
					Text(stringResource(RCoreDesign.string.action_save))
				}
			}
		}
	}
}