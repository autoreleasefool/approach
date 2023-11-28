package ca.josephroque.bowlingcompanion.feature.leaguedetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetails
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsTopBar
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun LeagueDetailsRoute(
	onBackPressed: () -> Unit,
	onEditSeries: (UUID) -> Unit,
	onAddSeries: (UUID) -> Unit,
	onShowSeriesDetails: (UUID) -> Unit,
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
						is LeagueDetailsScreenEvent.AddSeries -> onAddSeries(it.leagueId)
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

@Composable
private fun LeagueDetailsScreen(
	state: LeagueDetailsScreenUiState,
	onAction: (LeagueDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			LeagueDetailsTopBar(
				leagueName = when (state) {
					is LeagueDetailsScreenUiState.Loaded -> state.leagueDetails.leagueName
					LeagueDetailsScreenUiState.Loading -> ""
				},
				onAction = { onAction(LeagueDetailsScreenUiAction.LeagueDetails(it)) },
			)
		}
	) { padding ->
		when (state) {
			LeagueDetailsScreenUiState.Loading -> Unit
			is LeagueDetailsScreenUiState.Loaded -> LeagueDetails(
				state = state.leagueDetails,
				onAction = { onAction(LeagueDetailsScreenUiAction.LeagueDetails(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}

