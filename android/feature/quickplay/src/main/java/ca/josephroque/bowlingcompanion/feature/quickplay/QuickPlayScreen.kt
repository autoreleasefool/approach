package ca.josephroque.bowlingcompanion.feature.quickplay

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
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlay
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlayTopBar
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlayTopBarUiState
import kotlinx.coroutines.launch

@Composable
internal fun QuickPlayRoute(
	onDismiss: () -> Unit,
	onBeginRecordingSeries: (List<SeriesID>, GameID) -> Unit,
	onTeamLeaguesSelected: (TeamID, List<LeagueID>) -> Unit,
	onTeamEventsCreated: (TeamSeriesID, GameID) -> Unit,
	onPickBowler: (Set<BowlerID>, NavResultCallback<Set<BowlerID>>) -> Unit,
	onPickLeague: (BowlerID, LeagueID?, NavResultCallback<Set<LeagueID>>) -> Unit,
	onShowQuickPlayOnboarding: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: QuickPlayViewModel = hiltViewModel(),
) {
	val quickPlayScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						QuickPlayScreenEvent.Dismissed -> onDismiss()
						QuickPlayScreenEvent.ShowHowToUseQuickPlay -> onShowQuickPlayOnboarding()
						is QuickPlayScreenEvent.BeganRecordingSeries -> onBeginRecordingSeries(
							it.seriesIds,
							it.initialGameId,
						)
						is QuickPlayScreenEvent.TeamLeaguesSelected -> onTeamLeaguesSelected(it.teamId, it.leagueIds)
						is QuickPlayScreenEvent.TeamEventsCreated -> onTeamEventsCreated(
							it.teamSeriesId,
							it.initialGameId,
						)
						is QuickPlayScreenEvent.AddBowler -> onPickBowler(
							it.existingBowlers,
						) @JvmSerializableLambda { bowler ->
							viewModel.handleAction(QuickPlayScreenUiAction.AddedBowler(bowler.firstOrNull()))
						}
						is QuickPlayScreenEvent.EditLeague -> onPickLeague(
							it.bowlerId,
							it.leagueId,
						) @JvmSerializableLambda { leagues ->
							viewModel.handleAction(
								QuickPlayScreenUiAction.EditedLeague(it.bowlerId, leagues.firstOrNull()),
							)
						}
					}
				}
		}
	}

	QuickPlayScreen(
		state = quickPlayScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickPlayScreen(
	state: QuickPlayScreenUiState,
	onAction: (QuickPlayScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	LaunchedEffect(Unit) {
		onAction(QuickPlayScreenUiAction.DidAppear)
	}

	Scaffold(
		topBar = {
			QuickPlayTopBar(
				state = when (state) {
					QuickPlayScreenUiState.Loading -> QuickPlayTopBarUiState()
					is QuickPlayScreenUiState.Loaded -> state.topBar
				},
				onAction = { onAction(QuickPlayScreenUiAction.TopBar(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			QuickPlayScreenUiState.Loading -> Unit
			is QuickPlayScreenUiState.Loaded -> QuickPlay(
				state = state.quickPlay,
				onAction = { onAction(QuickPlayScreenUiAction.QuickPlay(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
