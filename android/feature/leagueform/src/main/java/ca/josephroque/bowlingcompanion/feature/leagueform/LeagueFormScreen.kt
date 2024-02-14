package ca.josephroque.bowlingcompanion.feature.leagueform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueForm
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormTopBar
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormUiAction
import kotlinx.coroutines.launch

@Composable
internal fun LeagueFormRoute(
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: LeagueFormViewModel = hiltViewModel(),
) {
	val leagueFormScreenState = viewModel.uiState.collectAsState().value

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						LeagueFormScreenEvent.Dismissed -> onDismiss()
					}
				}
		}
	}

	LeagueFormScreen(
		state = leagueFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LeagueFormScreen(
	state: LeagueFormScreenUiState,
	onAction: (LeagueFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(LeagueFormScreenUiAction.LoadLeague)
	}

	BackHandler {
		onAction(LeagueFormScreenUiAction.LeagueForm(LeagueFormUiAction.BackClicked))
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			LeagueFormTopBar(
				state = when (state) {
					LeagueFormScreenUiState.Loading -> LeagueFormTopBarUiState()
					is LeagueFormScreenUiState.Create -> state.topBar
					is LeagueFormScreenUiState.Edit -> state.topBar
				},
				onAction = { onAction(LeagueFormScreenUiAction.LeagueForm(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			LeagueFormScreenUiState.Loading -> Unit
			is LeagueFormScreenUiState.Create ->
				LeagueForm(
					state = state.form,
					onAction = { onAction(LeagueFormScreenUiAction.LeagueForm(it)) },
					modifier = Modifier.padding(padding),
				)
			is LeagueFormScreenUiState.Edit ->
				LeagueForm(
					state = state.form,
					onAction = { onAction(LeagueFormScreenUiAction.LeagueForm(it)) },
					modifier = Modifier.padding(padding),
				)
		}
	}
}
