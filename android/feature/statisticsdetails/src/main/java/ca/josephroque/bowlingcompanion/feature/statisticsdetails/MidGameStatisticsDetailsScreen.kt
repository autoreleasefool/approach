package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsList
import kotlinx.coroutines.launch

@Composable
internal fun MidGameStatisticsDetailsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: MidGameStatisticsDetailsViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						MidGameStatisticsDetailsScreenEvent.Dismissed -> onBackPressed()
					}
				}
		}
	}

	DisposableEffect(Unit) {
		onDispose {
			lifecycleOwner.lifecycleScope.launch {
				viewModel.handleAction(MidGameStatisticsDetailsScreenUiAction.OnDismissed)
			}
		}
	}

	MidGameStatisticsDetailsScreen(
		state = uiState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MidGameStatisticsDetailsScreen(
	state: MidGameStatisticsDetailsScreenUiState,
	onAction: (MidGameStatisticsDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			MidGameStatisticsDetailsTopBar(
				state = null,
				onAction = { onAction(MidGameStatisticsDetailsScreenUiAction.TopBar(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			MidGameStatisticsDetailsScreenUiState.Loading -> Unit
			is MidGameStatisticsDetailsScreenUiState.Loaded -> StatisticsDetailsList(
				state = state.list,
				onAction = { onAction(MidGameStatisticsDetailsScreenUiAction.List(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
