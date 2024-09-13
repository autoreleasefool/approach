package ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.StatisticsDetailsChartTopBar
import kotlinx.coroutines.launch

@Composable
internal fun StatisticsDetailsChartRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsDetailsChartViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						StatisticsDetailsChartScreenEvent.Dismissed -> onBackPressed()
					}
				}
		}
	}

	StatisticsDetailsChartScreen(
		state = uiState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun StatisticsDetailsChartScreen(
	state: StatisticsDetailsChartScreenUiState,
	onAction: (StatisticsDetailsChartScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			StatisticsDetailsChartTopBar(
				state = (state as? StatisticsDetailsChartScreenUiState.Loaded)?.chart,
				onAction = { onAction(StatisticsDetailsChartScreenUiAction.TopBar(it)) },
			)
		},
		modifier = modifier,
	) { padding ->
		when (state) {
			StatisticsDetailsChartScreenUiState.Loading -> Unit
			is StatisticsDetailsChartScreenUiState.Loaded -> StatisticsDetailsChart(
				state = state.chart,
				onAction = { onAction(StatisticsDetailsChartScreenUiAction.Chart(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
