package ca.josephroque.bowlingcompanion.feature.seriesform.prebowl

import androidx.activity.compose.BackHandler
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
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlForm
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormTopBar
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormTopBarUiState
import java.util.UUID
import kotlinx.coroutines.launch

@Composable
internal fun SeriesPreBowlFormRoute(
	onDismiss: () -> Unit,
	onShowSeriesPicker: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SeriesPreBowlFormViewModel = hiltViewModel(),
) {
	val seriesPreBowlFormScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is SeriesPreBowlFormScreenEvent.Dismissed -> onDismiss()
						is SeriesPreBowlFormScreenEvent.ShowSeriesPicker ->
							onShowSeriesPicker(it.leagueId, it.seriesId) { ids ->
								viewModel.handleAction(SeriesPreBowlFormScreenUiAction.SeriesUpdated(ids.firstOrNull()))
							}
					}
				}
		}
	}

	SeriesPreBowlFormScreen(
		state = seriesPreBowlFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeriesPreBowlFormScreen(
	state: SeriesPreBowlFormScreenUiState,
	onAction: (SeriesPreBowlFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	BackHandler {
		onAction(SeriesPreBowlFormScreenUiAction.TopBar(SeriesPreBowlFormTopBarUiAction.BackClicked))
	}
	
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	
	Scaffold(
		topBar = {
			SeriesPreBowlFormTopBar(
				state = (state as? SeriesPreBowlFormScreenUiState.Loaded)?.topBar
					?: SeriesPreBowlFormTopBarUiState(),
				onAction = { onAction(SeriesPreBowlFormScreenUiAction.TopBar(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			SeriesPreBowlFormScreenUiState.Loading -> Unit
			is SeriesPreBowlFormScreenUiState.Loaded -> SeriesPreBowlForm(
				state = state.form,
				onAction = { onAction(SeriesPreBowlFormScreenUiAction.Form(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
