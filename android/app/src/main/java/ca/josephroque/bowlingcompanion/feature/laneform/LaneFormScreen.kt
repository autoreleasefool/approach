package ca.josephroque.bowlingcompanion.feature.laneform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.database.model.asLaneListItem
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneForm
import ca.josephroque.bowlingcompanion.feature.laneslist.LaneItemRow
import java.util.UUID

@Composable
internal fun LaneFormRoute(
	onBackPressed: () -> Unit,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: LaneFormViewModel = hiltViewModel(),
) {
	val laneFormState by viewModel.lanes.collectAsStateWithLifecycle()

	when (laneFormState) {
		LaneFormUiState.Dismissed -> onDismiss()
		LaneFormUiState.Loading, is LaneFormUiState.Success -> Unit
	}

	LaunchedEffect(Unit) {
		viewModel.loadLanes()
	}

	LaneFormScreen(
		laneFormState = laneFormState,
		onBackPressed = onBackPressed,
		onDeleteLane = viewModel::deleteLane,
		onSave = viewModel::saveLanes,
		showAddLanesDialog = viewModel::showAddLanesDialog,
		onAddLanes = viewModel::addMultipleLanes,
		onLanesToAddChanged = viewModel::updateLanesToAdd,
		dismissAddLanesDialog = viewModel::dismissAddLanesDialog,
		showLaneLabelDialog = viewModel::showLaneLabelDialog,
		onLaneDialogLabelChanged = viewModel::updateLaneLabelDialogLabel,
		onLaneDialogPositionChanged = viewModel::updateLaneLabelDialogPosition,
		onToggleLaneDialogPositionDropDown = viewModel::toggleLaneLabelDialogPositionDropDown,
		finishLaneEdits = viewModel::dismissLaneLabelDialog,
		modifier = modifier,
	)
}

@Composable
internal fun LaneFormScreen(
	laneFormState: LaneFormUiState,
	onBackPressed: () -> Unit,
	onDeleteLane: (UUID) -> Unit,
	onSave: () -> Unit,
	showAddLanesDialog: () -> Unit,
	onLanesToAddChanged: (Int) -> Unit,
	onAddLanes: (Int) -> Unit,
	dismissAddLanesDialog: () -> Unit,
	showLaneLabelDialog: (UUID) -> Unit,
	onLaneDialogLabelChanged: (String) -> Unit,
	onLaneDialogPositionChanged: (LanePosition) -> Unit,
	onToggleLaneDialogPositionDropDown: (Boolean) -> Unit,
	finishLaneEdits: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			LaneFormTopBar(
				onBackPressed = onBackPressed,
				saveLanes = onSave,
			)
		},
		floatingActionButton = {
			FloatingActions(showAddLanesDialog = showAddLanesDialog)
		}
	) { padding ->
		Box(
			modifier = modifier.padding(padding),
		) {
			LaneForm(
				laneFormState = laneFormState,
				onAddLanes = onAddLanes,
				onLanesToAddChanged = onLanesToAddChanged,
				dismissAddLanesDialog = dismissAddLanesDialog,
				showLaneLabelDialog = showLaneLabelDialog,
				onLabelChanged = onLaneDialogLabelChanged,
				onPositionChanged = onLaneDialogPositionChanged,
				onTogglePositionDropDown = onToggleLaneDialogPositionDropDown,
				finishLaneEdits = finishLaneEdits,
				onDeleteLane = onDeleteLane,
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaneFormTopBar(
	onBackPressed: () -> Unit,
	saveLanes: () -> Unit,
) {
	TopAppBar(
		title = { Title() },
		navigationIcon = { BackButton(onClick = onBackPressed) },
		actions = { Actions(saveLanes = saveLanes) },
	)
}

@Composable
private fun Title() {
	Text(
		text = stringResource(R.string.lane_form_title),
		style = MaterialTheme.typography.titleMedium,
	)
}

@Composable
private fun Actions(
	saveLanes: () -> Unit,
) {
	Text(
		text = stringResource(R.string.action_save),
		modifier = Modifier
			.clickable(onClick = saveLanes)
			.padding(16.dp)
	)
}

@Composable
private fun FloatingActions(
	showAddLanesDialog: () -> Unit,
) {
	FloatingActionButton(onClick = showAddLanesDialog) {
		Icon(
			painter = painterResource(R.drawable.ic_list_add),
			contentDescription = stringResource(R.string.lane_form_add_multiple_lanes),
			tint = MaterialTheme.colorScheme.onSecondary,
		)
	}
}