package ca.josephroque.bowlingcompanion.feature.overview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.feature.bowlerslist.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.bowlerslist.bowlersList
import java.util.UUID

@Composable
internal fun OverviewRoute(
	onEditBowler: (UUID) -> Unit,
	onAddBowler: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: OverviewViewModel = hiltViewModel(),
) {
	val bowlersListState by viewModel.bowlersListState.collectAsStateWithLifecycle()

	OverviewScreen(
		bowlersListState = bowlersListState,
		onBowlerClick = viewModel::navigateToBowler,
		onFabClick = onAddBowler,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OverviewScreen(
	bowlersListState: BowlersListUiState,
	onBowlerClick: (UUID) -> Unit,
	onFabClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		floatingActionButtonPosition = FabPosition.End,
		floatingActionButton = {
			FloatingActionButton(onClick = onFabClick) {
				Icon(Icons.Filled.Add, stringResource(R.string.bowlers_list_add))
			}
		},
	) { padding ->
		LazyColumn(
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		) {
			bowlersList(
				bowlersListState = bowlersListState,
				onBowlerClick = onBowlerClick,
			)
		}
	}
}