package ca.josephroque.bowlingcompanion.feature.bowlerdetails

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
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetails
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsTopBar
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsTopBarUiState
import kotlinx.coroutines.launch

@Composable
internal fun BowlerDetailsRoute(
	onBackPressed: () -> Unit,
	onEditLeague: (LeagueID) -> Unit,
	onAddLeague: (BowlerID) -> Unit,
	onShowLeagueDetails: (LeagueID) -> Unit,
	onShowEventDetails: (LeagueID) -> Unit,
	onShowGearDetails: (GearID) -> Unit,
	onShowPreferredGearPicker: (Set<GearID>, NavResultCallback<Set<GearID>>) -> Unit,
	onEditStatisticsWidgets: (String, BowlerID) -> Unit,
	onShowWidgetStatistics: (TrackableFilter) -> Unit,
	onShowWidgetNotEnoughDataError: () -> Unit,
	onShowWidgetUnavailableError: () -> Unit,
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
						is BowlerDetailsScreenEvent.ShowEventDetails -> onShowEventDetails(it.leagueId)
						is BowlerDetailsScreenEvent.ShowGearDetails -> onShowGearDetails(it.gearId)
						is BowlerDetailsScreenEvent.EditStatisticsWidget -> onEditStatisticsWidgets(
							it.context,
							it.bowlerId,
						)
						is BowlerDetailsScreenEvent.ShowWidgetStatistics -> onShowWidgetStatistics(it.filter)
						is BowlerDetailsScreenEvent.ShowPreferredGearPicker -> onShowPreferredGearPicker(
							it.selectedGear,
						) @JvmSerializableLambda { selectedGear ->
							viewModel.handleAction(BowlerDetailsScreenUiAction.PreferredGearSelected(selectedGear))
						}
						BowlerDetailsScreenEvent.ShowWidgetNotEnoughDataError -> onShowWidgetNotEnoughDataError()
						BowlerDetailsScreenEvent.ShowWidgetUnavailableError -> onShowWidgetUnavailableError()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BowlerDetailsScreen(
	state: BowlerDetailsScreenUiState,
	onAction: (BowlerDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

	Scaffold(
		topBar = {
			BowlerDetailsTopBar(
				state = when (state) {
					BowlerDetailsScreenUiState.Loading -> BowlerDetailsTopBarUiState()
					is BowlerDetailsScreenUiState.Loaded -> state.bowler.topBar
				},
				onAction = { onAction(BowlerDetailsScreenUiAction.BowlerDetailsAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			BowlerDetailsScreenUiState.Loading -> Unit
			is BowlerDetailsScreenUiState.Loaded -> BowlerDetails(
				state = state.bowler,
				onAction = { onAction(BowlerDetailsScreenUiAction.BowlerDetailsAction(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
