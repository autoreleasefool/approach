package ca.josephroque.bowlingcompanion.feature.statisticsoverview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverview
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewTopBar
import java.util.UUID

@Composable
internal fun StatisticsOverviewRoute(
	onPickBowler: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickLeague: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickSeries: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickGame: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onShowStatistics: (TrackableFilter) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsOverviewViewModel = hiltViewModel(),
) {
	val statisticsOverviewState by viewModel.uiState.collectAsStateWithLifecycle()

	when (val event = viewModel.events.collectAsState().value) {
		is StatisticsOverviewScreenEvent.ShowStatistics -> {
			viewModel.handleAction(StatisticsOverviewScreenUiAction.FinishedNavigation)
			onShowStatistics(event.filter)
		}
		is StatisticsOverviewScreenEvent.EditBowler -> {
			viewModel.handleAction(StatisticsOverviewScreenUiAction.FinishedNavigation)
			onPickBowler(event.bowler) { ids ->
				viewModel.handleAction(StatisticsOverviewScreenUiAction.UpdatedBowler(ids.firstOrNull()))
			}
		}
		is StatisticsOverviewScreenEvent.EditLeague -> {
			viewModel.handleAction(StatisticsOverviewScreenUiAction.FinishedNavigation)
			onPickLeague(event.league) { ids ->
				viewModel.handleAction(StatisticsOverviewScreenUiAction.UpdatedLeague(ids.firstOrNull()))
			}
		}
		is StatisticsOverviewScreenEvent.EditSeries -> {
			viewModel.handleAction(StatisticsOverviewScreenUiAction.FinishedNavigation)
			onPickSeries(event.series) { ids ->
				viewModel.handleAction(StatisticsOverviewScreenUiAction.UpdatedSeries(ids.firstOrNull()))
			}
		}
		is StatisticsOverviewScreenEvent.EditGame -> {
			viewModel.handleAction(StatisticsOverviewScreenUiAction.FinishedNavigation)
			onPickGame(event.game) { ids ->
				viewModel.handleAction(StatisticsOverviewScreenUiAction.UpdatedGame(ids.firstOrNull()))
			}
		}
		null -> Unit
	}

	StatisticsOverviewScreen(
		state = statisticsOverviewState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun StatisticsOverviewScreen(
	state: StatisticsOverviewScreenUiState,
	onAction: (StatisticsOverviewScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			StatisticsOverviewTopBar()
		},
	) { padding ->
		when (state) {
			StatisticsOverviewScreenUiState.Loading -> Unit
			is StatisticsOverviewScreenUiState.Loaded -> StatisticsOverview(
				state = state.statisticsOverview,
				onAction = { onAction(StatisticsOverviewScreenUiAction.StatisticsOverviewAction(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}