package ca.josephroque.bowlingcompanion.feature.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.Bowler
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
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
			TopAppBar(
				colors = TopAppBarDefaults.topAppBarColors(),
				title = {
					Text(
						stringResource(R.string.overview_title),
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
				StatisticsCard(onClick = editStatisticsWidget)
			}

			bowlersList(
				bowlersListState = bowlersListState,
				onBowlerClick = onBowlerClick,
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StatisticsCard(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	ElevatedCard(
		onClick = onClick,
		colors = cardColors(
			containerColor = colorResource(R.color.purple_300),
			contentColor = colorResource(R.color.white),
		),
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			Image(
				painterResource(R.drawable.ic_analytics),
				contentDescription = null,
				modifier = Modifier
					.size(80.dp)
					.align(Alignment.BottomEnd)
					.alpha(0.3F)
			)
			Column {
				Text(
					stringResource(R.string.statistics_placeholder_title),
					fontSize = 24.sp,
				)
				Text(stringResource(R.string.statistics_placeholder_message))
			}
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