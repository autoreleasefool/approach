package ca.josephroque.bowlingcompanion.feature.overview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlerList
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.StatisticsWidgetPlaceholderCard
import java.util.UUID

@Composable
internal fun OverviewRoute(
	onEditBowler: (UUID) -> Unit,
	onAddBowler: () -> Unit,
	onShowBowlerDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: OverviewViewModel = hiltViewModel(),
) {
	val bowlersListState by viewModel.bowlersListState.collectAsStateWithLifecycle()

	OverviewScreen(
		bowlersListState = bowlersListState,
		onBowlerClick = onShowBowlerDetails,
		onAddBowler = onAddBowler,
		editStatisticsWidget = viewModel::editStatisticsWidget,
		modifier = modifier,
	)
}

@Composable
internal fun OverviewScreen(
	bowlersListState: BowlersListUiState,
	onBowlerClick: (UUID) -> Unit,
	onAddBowler: () -> Unit,
	editStatisticsWidget: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			OverviewTopBar(onAddBowler)
		}
	) { padding ->
		BowlerList(
			state = bowlersListState,
			onBowlerClick = onBowlerClick,
			onAddBowler = onAddBowler,
			header = {
				StatisticsWidgetPlaceholderCard(
					onClick = editStatisticsWidget,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
				)

				Text(
					text = stringResource(R.string.bowler_list_title),
					style = MaterialTheme.typography.titleLarge,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
				)
			},
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverviewTopBar(
	onAddBowler: () -> Unit,
) {
	CenterAlignedTopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.destination_overview),
				style = MaterialTheme.typography.titleLarge,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		},
		actions = {
			IconButton(onClick = onAddBowler) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.bowler_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	)
}

@Preview
@Composable
private fun OverviewPreview() {
	Surface {
		OverviewScreen(
			bowlersListState = BowlersListUiState.Success(listOf(
				BowlerListItem(id = UUID.randomUUID(), name = "Joseph", average = 120.0),
				BowlerListItem(id = UUID.randomUUID(), name = "Sarah", average = null),
			)),
			onBowlerClick = {},
			onAddBowler = {},
			editStatisticsWidget = {},
		)
	}
}