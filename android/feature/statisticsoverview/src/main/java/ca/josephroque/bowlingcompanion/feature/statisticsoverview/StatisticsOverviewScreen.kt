package ca.josephroque.bowlingcompanion.feature.statisticsoverview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverview
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewTopBar
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.ViewDetailedStatisticsFloatingActionButton
import kotlinx.coroutines.launch

@Composable
internal fun StatisticsOverviewRoute(
	onShowSourcePicker: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsOverviewViewModel = hiltViewModel(),
) {
	val statisticsOverviewState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						StatisticsOverviewScreenEvent.ShowSourcePicker -> onShowSourcePicker()
					}
				}
		}
	}

	StatisticsOverviewScreen(
		state = statisticsOverviewState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsOverviewScreen(
	state: StatisticsOverviewScreenUiState,
	onAction: (StatisticsOverviewScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	var fabHeight by remember { mutableIntStateOf(0) }
	val fabHeightInDp = with(LocalDensity.current) { fabHeight.toDp() }

	Scaffold(
		topBar = {
			StatisticsOverviewTopBar(
				scrollBehavior = scrollBehavior,
			)
		},
		floatingActionButton = {
			ViewDetailedStatisticsFloatingActionButton(
				onAction = { onAction(StatisticsOverviewScreenUiAction.StatisticsOverview(it)) },
				modifier = Modifier.onGloballyPositioned { fabHeight = it.size.height },
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			StatisticsOverviewScreenUiState.Loading -> Unit
			is StatisticsOverviewScreenUiState.Loaded -> StatisticsOverview(
				state = state.statisticsOverview,
				modifier = Modifier.padding(padding),
				contentPadding = PaddingValues(fabHeightInDp + 16.dp),
			)
		}
	}
}