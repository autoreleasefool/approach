package ca.josephroque.bowlingcompanion.feature.teamdetails

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
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetails
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBar
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBarUiState
import java.util.UUID
import kotlinx.coroutines.launch

@Composable
internal fun TeamDetailsRoute(
	onBackPressed: () -> Unit,
	onAddSeries: (UUID, NavResultCallback<UUID?>) -> Unit,
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
						is TeamDetailsScreenEvent.AddSeries -> onAddSeries(
							it.teamId,
						) @JvmSerializableLambda { seriesId ->
							if (seriesId != null) {
								viewModel.handleAction(TeamDetailsScreenUiAction.SeriesAdded(seriesId))
							}
						}
						is TeamDetailsScreenEvent.Dismissed -> onBackPressed()
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
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			TeamDetailsScreenUiState.Loading -> Unit
			is TeamDetailsScreenUiState.Loaded -> TeamDetails(
				state = state.teamDetails,
				onAction = { onAction(TeamDetailsScreenUiAction.TeamDetails(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
