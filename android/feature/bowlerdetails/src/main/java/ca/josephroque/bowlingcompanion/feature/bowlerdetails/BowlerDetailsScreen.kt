package ca.josephroque.bowlingcompanion.feature.bowlerdetails

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
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetails
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsTopBar
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsTopBarUiState
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun BowlerDetailsRoute(
	onBackPressed: () -> Unit,
	onEditLeague: (UUID) -> Unit,
	onAddLeague: (UUID) -> Unit,
	onShowLeagueDetails: (UUID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	onShowPreferredGearPicker: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: BowlerDetailsViewModel = hiltViewModel(),
) {
	val bowlerDetailsState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						BowlerDetailsScreenEvent.Dismissed -> onBackPressed()
						is BowlerDetailsScreenEvent.AddLeague -> onAddLeague(it.bowlerId)
						is BowlerDetailsScreenEvent.EditLeague -> onEditLeague(it.leagueId)
						is BowlerDetailsScreenEvent.ShowLeagueDetails -> onShowLeagueDetails(it.leagueId)
						is BowlerDetailsScreenEvent.ShowGearDetails -> onShowGearDetails(it.gearId)
						is BowlerDetailsScreenEvent.ShowPreferredGearPicker -> onShowPreferredGearPicker()
						is BowlerDetailsScreenEvent.EditStatisticsWidget -> Unit // TODO: Show edit widget screen
					}
				}
		}
	}

	BowlerDetailsScreen(
		state = bowlerDetailsState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
internal fun BowlerDetailsScreen(
	state: BowlerDetailsScreenUiState,
	onAction: (BowlerDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			BowlerDetailsTopBar(
				state = when (state) {
					BowlerDetailsScreenUiState.Loading -> BowlerDetailsTopBarUiState()
					is BowlerDetailsScreenUiState.Loaded -> state.bowler.topBar
				},
				onAction = { onAction(BowlerDetailsScreenUiAction.BowlerDetailsAction(it)) },
			)
		}
	) { padding ->
		when (state) {
			BowlerDetailsScreenUiState.Loading -> Unit
			is BowlerDetailsScreenUiState.Loaded -> BowlerDetails(
				state = state.bowler,
				onAction = { onAction(BowlerDetailsScreenUiAction.BowlerDetailsAction(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}

