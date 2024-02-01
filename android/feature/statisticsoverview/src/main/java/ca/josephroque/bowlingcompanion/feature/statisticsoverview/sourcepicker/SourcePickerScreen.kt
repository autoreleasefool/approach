package ca.josephroque.bowlingcompanion.feature.statisticsoverview.sourcepicker

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePicker
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerTopBar
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerUiAction
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun SourcePickerRoute(
	onDismiss: () -> Unit,
	onPickBowler: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickLeague: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickSeries: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickGame: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onShowStatistics: (TrackableFilter) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SourcePickerViewModel = hiltViewModel(),
) {
	val sourcePickerScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						SourcePickerScreenEvent.Dismissed -> onDismiss()
						is SourcePickerScreenEvent.ShowStatistics -> onShowStatistics(it.filter)
						is SourcePickerScreenEvent.EditBowler ->
							onPickBowler(it.bowler) { ids ->
								viewModel.handleAction(SourcePickerScreenUiAction.UpdatedBowler(ids.firstOrNull()))
							}
						is SourcePickerScreenEvent.EditLeague ->
							onPickLeague(it.bowler, it.league) { ids ->
								viewModel.handleAction(SourcePickerScreenUiAction.UpdatedLeague(ids.firstOrNull()))
							}
						is SourcePickerScreenEvent.EditSeries ->
							onPickSeries(it.league, it.series) { ids ->
								viewModel.handleAction(SourcePickerScreenUiAction.UpdatedSeries(ids.firstOrNull()))
							}
						is SourcePickerScreenEvent.EditGame ->
							onPickGame(it.series, it.game) { ids ->
								viewModel.handleAction(SourcePickerScreenUiAction.UpdatedGame(ids.firstOrNull()))
							}
					}
				}
		}
	}

	SourcePickerScreen(
		state = sourcePickerScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SourcePickerScreen(
	state: SourcePickerScreenUiState,
	onAction: (SourcePickerScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(SourcePickerScreenUiAction.DidAppear)
	}

	BackHandler {
		onAction(SourcePickerScreenUiAction.SourcePicker(SourcePickerUiAction.Dismissed))
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			SourcePickerTopBar(
				onAction = { onAction(SourcePickerScreenUiAction.SourcePicker(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			SourcePickerScreenUiState.Loading -> Unit
			is SourcePickerScreenUiState.Loaded -> SourcePicker(
				state = state.sourcePicker,
				onAction = { onAction(SourcePickerScreenUiAction.SourcePicker(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}