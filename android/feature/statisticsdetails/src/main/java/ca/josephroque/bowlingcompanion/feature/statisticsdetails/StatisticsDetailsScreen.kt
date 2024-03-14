package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.designsystem.components.LoadingState
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsList
import kotlinx.coroutines.launch

@Composable
internal fun StatisticsDetailsRoute(
	onBackPressed: () -> Unit,
	onShowStatisticChart: (TrackableFilter, StatisticID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsDetailsViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	val lifecycle = lifecycleOwner.lifecycle

	DisposableEffect(lifecycle) {
		lifecycle.addObserver(viewModel)
		onDispose {
			lifecycle.removeObserver(viewModel)
		}
	}

	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						StatisticsDetailsScreenEvent.Dismissed ->
							onBackPressed()
						is StatisticsDetailsScreenEvent.ShowStatisticChart ->
							onShowStatisticChart(it.filter, it.id)
					}
				}
		}
	}

	DisposableEffect(Unit) {
		onDispose {
			lifecycleOwner.lifecycleScope.launch {
				viewModel.handleAction(StatisticsDetailsScreenUiAction.OnDismissed)
			}
		}
	}

	StatisticsDetailsScreen(
		state = uiState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun StatisticsDetailsScreen(
	state: StatisticsDetailsScreenUiState,
	onAction: (StatisticsDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			StatisticsDetailsTopBar(
				onAction = { onAction(StatisticsDetailsScreenUiAction.TopBar(it)) },
			)
		},
		modifier = modifier,
	) { padding ->
		when (state) {
			StatisticsDetailsScreenUiState.Loading -> LoadingState(
				modifier = Modifier.padding(padding),
			)
			is StatisticsDetailsScreenUiState.Loaded -> StatisticsDetailsList(
				state = state.list,
				onAction = { onAction(StatisticsDetailsScreenUiAction.List(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
