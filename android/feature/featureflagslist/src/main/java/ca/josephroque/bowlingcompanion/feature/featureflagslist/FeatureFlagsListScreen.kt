package ca.josephroque.bowlingcompanion.feature.featureflagslist

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.featureflagslist.ui.FeatureFlagsList
import ca.josephroque.bowlingcompanion.feature.featureflagslist.ui.FeatureFlagsListTopBar
import kotlinx.coroutines.launch

@Composable
internal fun FeatureFlagsListRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: FeatureFlagsListViewModel = hiltViewModel(),
) {
	val featureFlagsListState by viewModel.uiState.collectAsStateWithLifecycle()
	
	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						FeatureFlagsListScreenEvent.Dismissed -> onBackPressed()
					}
				}
		}
	}

	FeatureFlagsListScreen(
		state = featureFlagsListState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureFlagsListScreen(
	state: FeatureFlagsListScreenUiState,
	onAction: (FeatureFlagsListScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	Scaffold(
		topBar = {
			FeatureFlagsListTopBar(
				onAction = { onAction(FeatureFlagsListScreenUiAction.List(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			FeatureFlagsListScreenUiState.Loading -> Unit
			is FeatureFlagsListScreenUiState.Loaded -> FeatureFlagsList(
				state = state.featureFlagsList,
				onAction = { onAction(FeatureFlagsListScreenUiAction.List(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
