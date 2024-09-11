package ca.josephroque.bowlingcompanion.feature.teamseriesdetails

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
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetails
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetailsTopBar
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetailsTopBarUiState
import kotlinx.coroutines.launch

@Composable
internal fun TeamSeriesDetailsRoute(
	onBackPressed: () -> Unit,
	onEditGame: (TeamSeriesID, GameID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: TeamSeriesDetailsViewModel = hiltViewModel(),
) {
	val teamSeriesDetailsScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						TeamSeriesDetailsScreenEvent.Dismissed -> onBackPressed()
						is TeamSeriesDetailsScreenEvent.EditGame -> onEditGame(it.teamSeriesId, it.gameId)
					}
				}
		}
	}

	TeamSeriesDetailsScreen(
		state = teamSeriesDetailsScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamSeriesDetailsScreen(
	state: TeamSeriesDetailsScreenUiState,
	onAction: (TeamSeriesDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

	Scaffold(
		topBar = {
			TeamSeriesDetailsTopBar(
				state = when (state) {
					TeamSeriesDetailsScreenUiState.Loading -> TeamSeriesDetailsTopBarUiState()
					is TeamSeriesDetailsScreenUiState.Loaded -> state.topBar
				},
				onAction = { onAction(TeamSeriesDetailsScreenUiAction.TopBar(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			TeamSeriesDetailsScreenUiState.Loading -> Unit
			is TeamSeriesDetailsScreenUiState.Loaded -> TeamSeriesDetails(
				state = state.teamSeriesDetails,
				onAction = { onAction(TeamSeriesDetailsScreenUiAction.TeamSeriesDetails(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
