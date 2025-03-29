package ca.josephroque.bowlingcompanion.feature.achievementslist

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
import ca.josephroque.bowlingcompanion.feature.achievementslist.ui.AchievementsList
import ca.josephroque.bowlingcompanion.feature.achievementslist.ui.AchievementsListTopBar

@Composable
internal fun AchievementsListRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AchievementsListViewModel = hiltViewModel(),
) {
	val achievementsListState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		viewModel.events
			.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
			.collect {
				when (it) {
					AchievementsListScreenEvent.Dismissed -> onBackPressed()
				}
			}
	}

	AchievementsListScreen(
		state = achievementsListState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementsListScreen(
	state: AchievementsListScreenUiState,
	onAction: (AchievementsListScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			AchievementsListTopBar(
				onAction = { onAction(AchievementsListScreenUiAction.TopBar(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			is AchievementsListScreenUiState.Loading -> Unit
			is AchievementsListScreenUiState.Loaded -> {
				AchievementsList(
					state = state.list,
					onAction = { onAction(AchievementsListScreenUiAction.List(it)) },
					modifier = modifier.padding(padding),
				)
			}
		}
	}
}