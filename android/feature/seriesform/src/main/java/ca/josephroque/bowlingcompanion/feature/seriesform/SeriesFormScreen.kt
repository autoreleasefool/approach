package ca.josephroque.bowlingcompanion.feature.seriesform

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
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesForm
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormTopBar
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormUiAction
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun SeriesFormRoute(
	onDismissWithResult: (UUID?) -> Unit,
	onEditAlley: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SeriesFormViewModel = hiltViewModel(),
) {
	val seriesFormScreenState = viewModel.uiState.collectAsState().value

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is SeriesFormScreenEvent.Dismissed -> onDismissWithResult(it.id)
						is SeriesFormScreenEvent.EditAlley ->
							onEditAlley(it.alleyId) { ids ->
								viewModel.handleAction(SeriesFormScreenUiAction.AlleyUpdated(ids.firstOrNull()))
							}
					}
				}
		}
	}

	SeriesFormScreen(
		state = seriesFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeriesFormScreen(
	state: SeriesFormScreenUiState,
	onAction: (SeriesFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(SeriesFormScreenUiAction.LoadSeries)
	}

	BackHandler {
		onAction(SeriesFormScreenUiAction.SeriesForm(SeriesFormUiAction.BackClicked))
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			SeriesFormTopBar(
				state = when (state) {
					is SeriesFormScreenUiState.Loading -> SeriesFormTopBarUiState()
					is SeriesFormScreenUiState.Create -> state.topBar
					is SeriesFormScreenUiState.Edit -> state.topBar
				},
				onAction = { onAction(SeriesFormScreenUiAction.SeriesForm(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			is SeriesFormScreenUiState.Loading -> Unit
			is SeriesFormScreenUiState.Create -> SeriesForm(
				state = state.form,
				onAction = { onAction(SeriesFormScreenUiAction.SeriesForm(it)) },
				modifier = Modifier.padding(padding),
			)
			is SeriesFormScreenUiState.Edit -> SeriesForm(
				state = state.form,
				onAction = { onAction(SeriesFormScreenUiAction.SeriesForm(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}