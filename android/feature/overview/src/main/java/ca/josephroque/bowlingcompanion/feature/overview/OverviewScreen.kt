package ca.josephroque.bowlingcompanion.feature.overview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.overview.ui.Overview
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewFloatingActionButton
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewTab
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewTopBar
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewTopBarUiState
import kotlinx.coroutines.launch

@Composable
internal fun OverviewRoute(
	onEditBowler: (BowlerID) -> Unit,
	onAddBowler: () -> Unit,
	onShowBowlerDetails: (BowlerID) -> Unit,
	onEditTeam: (TeamID) -> Unit,
	onAddTeam: () -> Unit,
	onShowTeamDetails: (TeamID) -> Unit,
	onEditStatisticsWidgets: (String) -> Unit,
	onShowWidgetStatistics: (TrackableFilter) -> Unit,
	onShowQuickPlay: () -> Unit,
	onResumeGame: (List<SeriesID>, GameID) -> Unit,
	onShowWidgetNotEnoughDataError: () -> Unit,
	onShowWidgetUnavailableError: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: OverviewViewModel = hiltViewModel(),
) {
	val overviewScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is OverviewScreenEvent.ShowTeamDetails -> onShowTeamDetails(it.id)
						is OverviewScreenEvent.ShowBowlerDetails -> onShowBowlerDetails(it.id)
						is OverviewScreenEvent.EditBowler -> onEditBowler(it.id)
						is OverviewScreenEvent.EditTeam -> onEditTeam(it.id)
						is OverviewScreenEvent.EditStatisticsWidget -> onEditStatisticsWidgets(it.context)
						is OverviewScreenEvent.ShowWidgetStatistics -> onShowWidgetStatistics(it.filter)
						is OverviewScreenEvent.ResumeGame -> onResumeGame(it.seriesIds, it.currentGameId)
						OverviewScreenEvent.ShowQuickPlay -> onShowQuickPlay()
						OverviewScreenEvent.AddBowler -> onAddBowler()
						OverviewScreenEvent.AddTeam -> onAddTeam()
						OverviewScreenEvent.ShowWidgetNotEnoughDataError -> onShowWidgetNotEnoughDataError()
						OverviewScreenEvent.ShowWidgetUnavailableError -> onShowWidgetUnavailableError()
					}
				}
		}
	}

	OverviewScreen(
		state = overviewScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverviewScreen(
	state: OverviewScreenUiState,
	onAction: (OverviewScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(OverviewScreenUiAction.DidAppear)
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	var fabHeight by remember { mutableIntStateOf(0) }
	val fabHeightInDp = with(LocalDensity.current) { fabHeight.toDp() }
	val snackBarHostState = remember { SnackbarHostState() }

	val isGameInProgressSnackBarVisible = state is OverviewScreenUiState.Loaded &&
		state.isGameInProgressSnackBarVisible
	if (isGameInProgressSnackBarVisible) {
		val gameInProgressSnackBarMessage =
			stringResource(ca.josephroque.bowlingcompanion.feature.overview.ui.R.string.game_in_progress)
		val resumeGame =
			stringResource(ca.josephroque.bowlingcompanion.feature.overview.ui.R.string.resume_game)

		LaunchedEffect(Unit) {
			val result = snackBarHostState.showSnackbar(
				message = gameInProgressSnackBarMessage,
				actionLabel = resumeGame,
				withDismissAction = true,
				duration = SnackbarDuration.Indefinite,
			)

			when (result) {
				SnackbarResult.Dismissed -> onAction(OverviewScreenUiAction.GameInProgressSnackBarDismissed)
				SnackbarResult.ActionPerformed -> onAction(OverviewScreenUiAction.ResumeGameInProgressClicked)
			}
		}
	}

	Scaffold(
		topBar = {
			OverviewTopBar(
				state = when (state) {
					OverviewScreenUiState.Loading -> OverviewTopBarUiState.BowlerTab()
					is OverviewScreenUiState.Loaded -> state.topBar
				},
				onAction = { onAction(OverviewScreenUiAction.OverviewAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		floatingActionButton = {
			if (state is OverviewScreenUiState.Loaded && state.overview.tab == OverviewTab.BOWLERS) {
				OverviewFloatingActionButton(
					modifier = Modifier.onGloballyPositioned { fabHeight = it.size.height },
					onAction = { onAction(OverviewScreenUiAction.OverviewAction(it)) },
				)
			}
		},
		snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			OverviewScreenUiState.Loading -> Unit
			is OverviewScreenUiState.Loaded -> Overview(
				state = state.overview,
				onAction = { onAction(OverviewScreenUiAction.OverviewAction(it)) },
				modifier = Modifier.padding(padding),
				contentPadding = PaddingValues(bottom = fabHeightInDp + 16.dp),
			)
		}
	}
}
