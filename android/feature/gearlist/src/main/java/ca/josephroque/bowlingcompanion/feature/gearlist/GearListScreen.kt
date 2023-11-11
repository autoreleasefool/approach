package ca.josephroque.bowlingcompanion.feature.gearlist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.LoadingState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearList
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListTopBar
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiAction
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import java.util.UUID

@Composable
internal fun GearListRoute(
	onBackPressed: () -> Unit,
	onEditGear: (UUID) -> Unit,
	onAddGear: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GearListViewModel = hiltViewModel(),
) {
	val gearListScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	when (val event = viewModel.events.collectAsState().value) {
		GearListScreenEvent.Dismissed -> onBackPressed()
		is GearListScreenEvent.NavigateToAddGear -> {
			viewModel.handleAction(GearListScreenUiAction.HandledNavigation)
			onAddGear()
		}
		is GearListScreenEvent.NavigateToEditGear -> {
			viewModel.handleAction(GearListScreenUiAction.HandledNavigation)
			onEditGear(event.id)
		}
		null -> Unit
	}

	GearListScreen(
		state = gearListScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun GearListScreen(
	state: GearListScreenUiState,
	onAction: (GearListScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	when (state) {
		GearListScreenUiState.Loading -> LoadingState()
		is GearListScreenUiState.Loaded ->
			GearListScreen(
				gearListState = state.gearList,
				topBarState = state.topBar,
				onAction = { onAction(GearListScreenUiAction.GearListAction(it)) },
				modifier = modifier,
			)
	}
}

@Composable
private fun GearListScreen(
	gearListState: GearListUiState,
	topBarState: GearListTopBarUiState,
	onAction: (GearListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			GearListTopBar(
				state = topBarState,
				onAction = onAction,
			)
		}
	) { padding ->
		GearList(
			state = gearListState,
			onAction = onAction,
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		)
	}
}