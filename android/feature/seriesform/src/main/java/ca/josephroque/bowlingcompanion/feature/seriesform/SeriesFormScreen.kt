package ca.josephroque.bowlingcompanion.feature.seriesform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultViewModel
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesForm
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormTopBar
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormUiAction
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
internal fun SeriesFormRoute(
	onDismissWithResult: (SeriesID?) -> Unit,
	onStartTeamSeries: (TeamSeriesID, GameID) -> Unit,
	onEditAlley: (AlleyID?, ResourcePickerResultKey) -> Unit,
	onEditLeague: (BowlerID, LeagueID, ResourcePickerResultKey) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SeriesFormViewModel = hiltViewModel(),
	resultViewModel: ResourcePickerResultViewModel = hiltViewModel(),
) {
	val seriesFormScreenState = viewModel.uiState.collectAsState().value

	LaunchedEffect(Unit) {
		resultViewModel.getSelectedIds(SERIES_FORM_ALLEY_PICKER_RESULT_KEY) { AlleyID(it) }
			.onEach { viewModel.handleAction(SeriesFormScreenUiAction.AlleyUpdated(it.firstOrNull())) }
			.launchIn(this)
	}

	LaunchedEffect(Unit) {
		resultViewModel.getSelectedIds(SERIES_FORM_LEAGUE_PICKER_RESULT_KEY) { LeagueID(it) }
			.onEach { viewModel.handleAction(SeriesFormScreenUiAction.LeagueUpdated(it.firstOrNull())) }
			.launchIn(this)
	}

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is SeriesFormScreenEvent.Dismissed -> onDismissWithResult(it.seriesId)
						is SeriesFormScreenEvent.EditAlley ->
							onEditAlley(it.alleyId, SERIES_FORM_ALLEY_PICKER_RESULT_KEY)
						is SeriesFormScreenEvent.EditLeague ->
							onEditLeague(it.bowlerId, it.leagueId, SERIES_FORM_LEAGUE_PICKER_RESULT_KEY)
						is SeriesFormScreenEvent.StartTeamSeries -> onStartTeamSeries(
							it.teamSeriesId,
							it.initialGameId,
						)
					}
				}
		}
	}

	SeriesFormScreen(
		state = seriesFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeriesFormScreen(
	state: SeriesFormScreenUiState,
	onAction: (SeriesFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(SeriesFormScreenUiAction.LoadSeries)
	}

	BackHandler {
		onAction(SeriesFormScreenUiAction.SeriesForm(SeriesFormUiAction.BackClicked))
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			SeriesFormTopBar(
				state = when (state) {
					is SeriesFormScreenUiState.Loading -> SeriesFormTopBarUiState()
					is SeriesFormScreenUiState.Create -> state.topBar
					is SeriesFormScreenUiState.Edit -> state.topBar
					is SeriesFormScreenUiState.TeamCreate -> state.topBar
				},
				onAction = { onAction(SeriesFormScreenUiAction.SeriesForm(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			is SeriesFormScreenUiState.Loading -> Unit
			is SeriesFormScreenUiState.TeamCreate -> SeriesForm(
				state = state.form,
				onAction = { onAction(SeriesFormScreenUiAction.SeriesForm(it)) },
				modifier = Modifier.padding(padding),
			)
			is SeriesFormScreenUiState.Create -> SeriesForm(
				state = state.form,
				onAction = { onAction(SeriesFormScreenUiAction.SeriesForm(it)) },
				modifier = Modifier.padding(padding),
			)
			is SeriesFormScreenUiState.Edit -> SeriesForm(
				state = state.form,
				onAction = { onAction(SeriesFormScreenUiAction.SeriesForm(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
