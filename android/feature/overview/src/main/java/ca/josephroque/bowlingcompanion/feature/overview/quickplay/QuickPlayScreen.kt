package ca.josephroque.bowlingcompanion.feature.overview.quickplay

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
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.QuickPlay
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.QuickPlayTopBar
import java.util.UUID
import kotlinx.coroutines.launch

@Composable
internal fun QuickPlayRoute(
	onDismiss: () -> Unit,
	onBeginRecording: (List<SeriesID>, UUID) -> Unit,
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
						is QuickPlayScreenEvent.BeganRecording -> onBeginRecording(it.seriesIds, it.initialGameId)
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
				onAction = { onAction(QuickPlayScreenUiAction.QuickPlay(it)) },
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
