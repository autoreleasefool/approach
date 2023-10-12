package ca.josephroque.bowlingcompanion.feature.overview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import ca.josephroque.bowlingcompanion.feature.bowlerslist.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.bowlerslist.bowlersList
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

@OptIn(ExperimentalMaterial3Api::class)
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
			CenterAlignedTopAppBar(
				colors = TopAppBarDefaults.topAppBarColors(),
				title = {
					Text(
						text = stringResource(R.string.overview_title),
						style = MaterialTheme.typography.titleLarge,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)
				},
				actions = {
					IconButton(onClick = onAddBowler) {
						Icon(
							imageVector = Icons.Filled.Add,
							contentDescription = stringResource(R.string.bowler_list_add)
						)
					}
				}
			)
		}
	) { padding ->
		LazyColumn(
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		) {
			item {
				StatisticsWidgetPlaceholderCard(
					onClick = editStatisticsWidget,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
				)
			}

			item {
				Text(
					text = stringResource(R.string.bowler_list_title),
					style = MaterialTheme.typography.titleLarge,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
				)
			}

			bowlersList(
				bowlersListState = bowlersListState,
				onBowlerClick = onBowlerClick,
			)
		}
	}
}



@Preview
@Composable
fun OverviewPreview() {
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