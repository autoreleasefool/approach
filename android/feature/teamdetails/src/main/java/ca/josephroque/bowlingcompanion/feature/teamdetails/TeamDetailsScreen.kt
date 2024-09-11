package ca.josephroque.bowlingcompanion.feature.teamdetails

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetails
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsFloatingActionButton
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBar
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBarUiState
import kotlinx.coroutines.launch

@Composable
internal fun TeamDetailsRoute(
	onBackPressed: () -> Unit,
	onAddSeries: (TeamID) -> Unit,
	onViewSeries: (TeamSeriesID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: TeamDetailsViewModel = hiltViewModel(),
) {
	val teamDetailsScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is TeamDetailsScreenEvent.AddSeries -> onAddSeries(it.teamId)
						is TeamDetailsScreenEvent.Dismissed -> onBackPressed()
						is TeamDetailsScreenEvent.ViewSeries -> onViewSeries(it.teamSeriesId)
					}
				}
		}
	}
	
	TeamDetailsScreen(
		state = teamDetailsScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamDetailsScreen(
	state: TeamDetailsScreenUiState,
	onAction: (TeamDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

	var fabHeight by remember { mutableIntStateOf(0) }
	val fabHeightInDp = with(LocalDensity.current) { fabHeight.toDp() }
	
	Scaffold(
		topBar = {
			TeamDetailsTopBar(
				state = when (state) {
					is TeamDetailsScreenUiState.Loaded -> state.topBar
					TeamDetailsScreenUiState.Loading -> TeamDetailsTopBarUiState()
				},
				onAction = { onAction(TeamDetailsScreenUiAction.TopBar(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		floatingActionButton = {
			TeamDetailsFloatingActionButton(
				onAction = { onAction(TeamDetailsScreenUiAction.FloatingActionButton(it)) },
				modifier = Modifier.onGloballyPositioned { fabHeight = it.size.height },
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			TeamDetailsScreenUiState.Loading -> Unit
			is TeamDetailsScreenUiState.Loaded -> TeamDetails(
				state = state.teamDetails,
				onAction = { onAction(TeamDetailsScreenUiAction.TeamDetails(it)) },
				modifier = Modifier.padding(padding),
				contentPadding = PaddingValues(bottom = fabHeightInDp + 16.dp),
			)
		}
	}
}
