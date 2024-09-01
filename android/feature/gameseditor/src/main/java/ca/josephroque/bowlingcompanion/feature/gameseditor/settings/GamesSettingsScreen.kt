package ca.josephroque.bowlingcompanion.feature.gameseditor.settings

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
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettings
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsTopBar
import kotlinx.coroutines.launch

@Composable
internal fun GamesSettingsRoute(
	onDismissWithResult: (Pair<List<SeriesID>, GameID>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GamesSettingsViewModel = hiltViewModel(),
) {
	val gamesSettingsScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is GamesSettingsScreenEvent.DismissedWithResult -> onDismissWithResult(
							it.series to it.currentGame,
						)
					}
				}
		}
	}

	GamesSettingsScreen(
		state = gamesSettingsScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GamesSettingsScreen(
	state: GamesSettingsScreenUiState,
	onAction: (GamesSettingsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			GamesSettingsTopBar(
				onAction = { onAction(GamesSettingsScreenUiAction.GamesSettings(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			GamesSettingsScreenUiState.Loading -> Unit
			is GamesSettingsScreenUiState.Loaded -> GamesSettings(
				state = state.gamesSettings,
				onAction = { onAction(GamesSettingsScreenUiAction.GamesSettings(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
