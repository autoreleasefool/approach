package ca.josephroque.bowlingcompanion.feature.seriesdetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetails
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsTopBar
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsTopBarUiState
import kotlinx.coroutines.launch
import java.util.UUID

data class EditGameArgs(
	val seriesId: UUID,
	val gameId: UUID,
)

@Composable
internal fun SeriesDetailsRoute(
	onBackPressed: () -> Unit,
	onEditGame: (EditGameArgs) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SeriesDetailsViewModel = hiltViewModel(),
) {
	val seriesDetailsScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						SeriesDetailsScreenEvent.Dismissed -> onBackPressed()
						is SeriesDetailsScreenEvent.EditGame -> onEditGame(it.args)
					}
				}
		}
	}

	SeriesDetailsScreen(
		state = seriesDetailsScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
internal fun SeriesDetailsScreen(
	state: SeriesDetailsScreenUiState,
	onAction: (SeriesDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			SeriesDetailsTopBar(
				state = when (state) {
					SeriesDetailsScreenUiState.Loading -> SeriesDetailsTopBarUiState()
					is SeriesDetailsScreenUiState.Loaded -> state.seriesDetails.topBar
				},
				onAction = { onAction(SeriesDetailsScreenUiAction.SeriesDetails(it)) },
			)
		}
	) { padding ->
		when (state) {
			SeriesDetailsScreenUiState.Loading -> Unit
			is SeriesDetailsScreenUiState.Loaded -> SeriesDetails(
				state = state.seriesDetails,
				onAction = { onAction(SeriesDetailsScreenUiAction.SeriesDetails(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}
