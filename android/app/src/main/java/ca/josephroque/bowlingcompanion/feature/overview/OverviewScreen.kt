package ca.josephroque.bowlingcompanion.feature.overview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.bowlerslist.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.bowlerslist.bowlersList
import java.util.UUID

@Composable
internal fun OverviewRoute(
	modifier: Modifier = Modifier,
	viewModel: OverviewViewModel = hiltViewModel(),
) {
	val bowlersListState by viewModel.bowlersListState.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		viewModel.loadBowlers()
	}

	OverviewScreen(
		bowlersListState = bowlersListState,
		onBowlerClick = viewModel::navigateToBowler,
		modifier = modifier,
	)
}

@Composable
internal fun OverviewScreen(
	bowlersListState: BowlersListUiState,
	onBowlerClick: (UUID) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(
		modifier = modifier.fillMaxSize(),
	) {
		bowlersList(
			bowlersListState = bowlersListState,
			onBowlerClick = onBowlerClick,
		)
	}
}