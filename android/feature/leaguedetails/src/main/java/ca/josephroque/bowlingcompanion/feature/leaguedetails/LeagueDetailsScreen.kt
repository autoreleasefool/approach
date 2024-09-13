package ca.josephroque.bowlingcompanion.feature.leaguedetails

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
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetails
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsTopBar
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsTopBarUiState
import kotlinx.coroutines.launch

@Composable
internal fun LeagueDetailsRoute(
	onBackPressed: () -> Unit,
	onEditSeries: (SeriesID) -> Unit,
	onAddSeries: (LeagueID, NavResultCallback<SeriesID?>) -> Unit,
	onShowSeriesDetails: (SeriesID) -> Unit,
	onUsePreBowl: (LeagueID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: LeagueDetailsViewModel = hiltViewModel(),
) {
	val leagueDetailsScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is LeagueDetailsScreenEvent.AddSeries -> onAddSeries(
							it.leagueId,
						) @JvmSerializableLambda { seriesId ->
							if (seriesId != null) {
								viewModel.handleAction(LeagueDetailsScreenUiAction.SeriesAdded(seriesId))
							}
						}
						is LeagueDetailsScreenEvent.UsePreBowl -> onUsePreBowl(it.leagueId)
						is LeagueDetailsScreenEvent.EditSeries -> onEditSeries(it.seriesId)
						is LeagueDetailsScreenEvent.Dismissed -> onBackPressed()
						is LeagueDetailsScreenEvent.ShowSeriesDetails -> onShowSeriesDetails(it.seriesId)
					}
				}
		}
	}

	LeagueDetailsScreen(
		state = leagueDetailsScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeagueDetailsScreen(
	state: LeagueDetailsScreenUiState,
	onAction: (LeagueDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

	Scaffold(
		topBar = {
			LeagueDetailsTopBar(
				state = when (state) {
					is LeagueDetailsScreenUiState.Loaded -> state.leagueDetails.topBar
					LeagueDetailsScreenUiState.Loading -> LeagueDetailsTopBarUiState()
				},
				onAction = { onAction(LeagueDetailsScreenUiAction.LeagueDetails(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			LeagueDetailsScreenUiState.Loading -> Unit
			is LeagueDetailsScreenUiState.Loaded -> LeagueDetails(
				state = state.leagueDetails,
				onAction = { onAction(LeagueDetailsScreenUiAction.LeagueDetails(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
