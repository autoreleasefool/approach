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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultViewModel
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePicker
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerTopBar
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerTopBarUiState
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerUiAction
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
internal fun SourcePickerRoute(
	onDismiss: () -> Unit,
	onPickTeam: (TeamID?, ResourcePickerResultKey) -> Unit,
	onPickBowler: (BowlerID?, ResourcePickerResultKey) -> Unit,
	onPickLeague: (BowlerID, LeagueID?, ResourcePickerResultKey) -> Unit,
	onPickSeries: (LeagueID, SeriesID?, ResourcePickerResultKey) -> Unit,
	onPickGame: (SeriesID, GameID?, ResourcePickerResultKey) -> Unit,
	onShowStatistics: (TrackableFilter) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SourcePickerViewModel = hiltViewModel(),
	resultViewModel: ResourcePickerResultViewModel = hiltViewModel(),
) {
	val sourcePickerScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		resultViewModel.getSelectedIds(SOURCE_PICKER_TEAM_RESULT_KEY) { TeamID(it) }
			.onEach { viewModel.handleAction(SourcePickerScreenUiAction.UpdatedTeam(it.firstOrNull())) }
			.launchIn(this)

		resultViewModel.getSelectedIds(SOURCE_PICKER_BOWLER_RESULT_KEY) { BowlerID(it) }
			.onEach { viewModel.handleAction(SourcePickerScreenUiAction.UpdatedBowler(it.firstOrNull())) }
			.launchIn(this)

		resultViewModel.getSelectedIds(SOURCE_PICKER_LEAGUE_RESULT_KEY) { LeagueID(it) }
			.onEach { viewModel.handleAction(SourcePickerScreenUiAction.UpdatedLeague(it.firstOrNull())) }
			.launchIn(this)

		resultViewModel.getSelectedIds(SOURCE_PICKER_SERIES_RESULT_KEY) { SeriesID(it) }
			.onEach { viewModel.handleAction(SourcePickerScreenUiAction.UpdatedSeries(it.firstOrNull())) }
			.launchIn(this)

		resultViewModel.getSelectedIds(SOURCE_PICKER_GAME_RESULT_KEY) { GameID(it) }
			.onEach { viewModel.handleAction(SourcePickerScreenUiAction.UpdatedGame(it.firstOrNull())) }
			.launchIn(this)
	}

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						SourcePickerScreenEvent.Dismissed -> onDismiss()
						is SourcePickerScreenEvent.ShowStatistics -> onShowStatistics(it.filter)
						is SourcePickerScreenEvent.EditTeam ->
							onPickTeam(it.team, SOURCE_PICKER_TEAM_RESULT_KEY)
						is SourcePickerScreenEvent.EditBowler ->
							onPickBowler(it.bowler, SOURCE_PICKER_BOWLER_RESULT_KEY)
						is SourcePickerScreenEvent.EditLeague ->
							onPickLeague(it.bowler, it.league, SOURCE_PICKER_LEAGUE_RESULT_KEY)
						is SourcePickerScreenEvent.EditSeries ->
							onPickSeries(it.league, it.series, SOURCE_PICKER_SERIES_RESULT_KEY)
						is SourcePickerScreenEvent.EditGame ->
							onPickGame(it.series, it.game, SOURCE_PICKER_GAME_RESULT_KEY)
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
				state = when (state) {
					SourcePickerScreenUiState.Loading -> SourcePickerTopBarUiState()
					is SourcePickerScreenUiState.Loaded -> state.topBar
				},
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
