package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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

@Composable
private fun StatisticsDetailsScreen(
	state: StatisticsDetailsScreenUiState,
	onAction: (StatisticsDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(

	) { padding ->
		when (state) {
			StatisticsDetailsScreenUiState.Loading -> Unit
			is StatisticsDetailsScreenUiState.Loaded -> StatisticsDetailsList(
				state = state.list,
				onAction = { onAction(StatisticsDetailsScreenUiAction.StatisticsDetailsListAction(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}