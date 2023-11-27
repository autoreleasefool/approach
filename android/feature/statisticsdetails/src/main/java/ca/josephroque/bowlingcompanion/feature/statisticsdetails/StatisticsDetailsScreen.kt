package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChart
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsList

@Composable
fun StatisticsDetailsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsDetailsViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	when (viewModel.events.collectAsState().value) {
		null -> Unit
		StatisticsDetailsScreenEvent.Dismissed -> onBackPressed()
	}

	StatisticsDetailsScreen(
		state = uiState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsDetailsScreen(
	state: StatisticsDetailsScreenUiState,
	onAction: (StatisticsDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scaffoldState = rememberBottomSheetScaffoldState()

	BottomSheetScaffold(
		scaffoldState = scaffoldState,
		topBar = {
			StatisticsDetailsTopBar(
				onAction = { onAction(StatisticsDetailsScreenUiAction.TopBarAction(it)) },
			)
		},
		sheetContent = {
			when (state) {
				StatisticsDetailsScreenUiState.Loading -> Unit
				is StatisticsDetailsScreenUiState.Loaded -> StatisticsDetailsList(
					state = state.list,
					onAction = { onAction(StatisticsDetailsScreenUiAction.ListAction(it)) },
					modifier = Modifier.fillMaxHeight(0.5f),
				)
			}
		},
	) { padding ->
		when (state) {
			StatisticsDetailsScreenUiState.Loading -> Unit
			is StatisticsDetailsScreenUiState.Loaded -> StatisticsDetailsChart(
				state = state.chart,
				onAction = { onAction(StatisticsDetailsScreenUiAction.ChartAction(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}