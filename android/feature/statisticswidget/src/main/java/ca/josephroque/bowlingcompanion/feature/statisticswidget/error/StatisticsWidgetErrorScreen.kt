package ca.josephroque.bowlingcompanion.feature.statisticswidget.error

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
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.error.StatisticsWidgetError
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.error.StatisticsWidgetErrorTopBar
import kotlinx.coroutines.launch

@Composable
internal fun StatisticsWidgetErrorRoute(
	onDismissed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsWidgetErrorViewModel = hiltViewModel(),
) {
	val statisticsWidgetErrorState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						StatisticsWidgetErrorScreenEvent.Dismissed -> onDismissed()
					}
				}
		}
	}
	
	StatisticsWidgetErrorScreen(
		state = statisticsWidgetErrorState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsWidgetErrorScreen(
	state: StatisticsWidgetErrorScreenUiState,
	onAction: (StatisticsWidgetErrorScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			StatisticsWidgetErrorTopBar(
				onAction = { onAction(StatisticsWidgetErrorScreenUiAction.TopBar(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		StatisticsWidgetError(
			state = state.statisticsWidgetError,
			modifier = Modifier.padding(padding),
		)
	}
}
