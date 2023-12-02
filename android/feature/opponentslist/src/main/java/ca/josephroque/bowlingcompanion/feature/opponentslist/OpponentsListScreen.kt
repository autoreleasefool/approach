package ca.josephroque.bowlingcompanion.feature.opponentslist

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
import ca.josephroque.bowlingcompanion.feature.opponentslist.ui.OpponentsList
import ca.josephroque.bowlingcompanion.feature.opponentslist.ui.OpponentsListTopBar
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun OpponentsListRoute(
	onBackPressed: () -> Unit,
	onAddOpponent: () -> Unit,
	onOpenOpponentDetails: (UUID) -> Unit,
	onEditOpponent: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: OpponentsListViewModel = hiltViewModel(),
) {
	val opponentsListState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						OpponentsListScreenEvent.Dismissed -> onBackPressed()
						OpponentsListScreenEvent.AddOpponent -> onAddOpponent()
						is OpponentsListScreenEvent.EditOpponent -> onEditOpponent(it.id)
						is OpponentsListScreenEvent.ShowOpponentDetails -> onOpenOpponentDetails(it.id)
					}
				}
		}
	}

	OpponentsListScreen(
		state = opponentsListState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OpponentsListScreen(
	state: OpponentsListScreenUiState,
	onAction: (OpponentsListScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			OpponentsListTopBar(
				onAction = { onAction(OpponentsListScreenUiAction.OpponentsListAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			OpponentsListScreenUiState.Loading -> Unit
			is OpponentsListScreenUiState.Loaded -> {
				OpponentsList(
					state = state.list,
					onAction = { onAction(OpponentsListScreenUiAction.OpponentsListAction(it)) },
					modifier = Modifier.padding(padding),
				)
			}
		}
	}
}

