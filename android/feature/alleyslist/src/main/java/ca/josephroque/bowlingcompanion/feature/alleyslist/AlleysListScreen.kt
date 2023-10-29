package ca.josephroque.bowlingcompanion.feature.alleyslist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListTopBar
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.alleysList
import java.util.UUID

@Composable
internal fun AlleysListRoute(
	onBackPressed: () -> Unit,
	onEditAlley: (UUID) -> Unit,
	onAddAlley: () -> Unit,
	onShowAlleyDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AlleysListViewModel = hiltViewModel(),
) {
	val alleysListState by viewModel.alleysListState.collectAsStateWithLifecycle()

	AlleysListScreen(
		alleysListState = alleysListState,
		onBackPressed = onBackPressed,
		onAddAlley = onAddAlley,
		onAlleyClick = onShowAlleyDetails,
		modifier = modifier,
	)
}

@Composable
internal fun AlleysListScreen(
	alleysListState: AlleysListUiState,
	onBackPressed: () -> Unit,
	onAlleyClick: (UUID) -> Unit,
	onAddAlley: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AlleysListTopBar(onAddAlley = onAddAlley, onBackPressed = onBackPressed)
		}
	) { padding ->
		LazyColumn(
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		) {
			alleysList(
				alleysListState = alleysListState,
				onAlleyClick = onAlleyClick,
			)
		}
	}
}