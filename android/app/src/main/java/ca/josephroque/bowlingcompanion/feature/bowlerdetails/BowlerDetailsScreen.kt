package ca.josephroque.bowlingcompanion.feature.bowlerdetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.feature.leagueslist.LeaguesListUiState
import ca.josephroque.bowlingcompanion.feature.leagueslist.leaguesList
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.StatisticsWidgetPlaceholderCard
import java.util.UUID

@Composable
internal fun BowlerDetailsRoute(
	onBackPressed: () -> Unit,
	onEditLeague: (UUID) -> Unit,
	onAddLeague: (UUID) -> Unit,
	onShowLeagueDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: BowlerDetailsViewModel = hiltViewModel(),
) {
	val bowlerDetailsState by viewModel.bowlerDetailsState.collectAsStateWithLifecycle()
	val leaguesListState by viewModel.leaguesListState.collectAsStateWithLifecycle()

	BowlerDetailsScreen(
		bowlerDetailsState = bowlerDetailsState,
		leaguesListState = leaguesListState,
		onBackPressed = onBackPressed,
		onLeagueClick = onShowLeagueDetails,
		onAddLeague = { bowlerDetailsState.bowlerId()?.let { onAddLeague(it) } },
		editStatisticsWidget = viewModel::editStatisticsWidget,
		modifier = modifier,
	)
}

@Composable
internal fun BowlerDetailsScreen(
	bowlerDetailsState: BowlerDetailsUiState,
	leaguesListState: LeaguesListUiState,
	onBackPressed: () -> Unit,
	onLeagueClick: (UUID) -> Unit,
	onAddLeague: () -> Unit,
	editStatisticsWidget: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			BowlerDetailsTopBar(
				bowlerDetailsState = bowlerDetailsState,
				onAddLeague = onAddLeague,
				onBackPressed = onBackPressed
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
					text = stringResource(R.string.league_list_title),
					style = MaterialTheme.typography.titleLarge,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
				)
			}

			leaguesList(
				leaguesListState = leaguesListState,
				onLeagueClick = onLeagueClick,
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BowlerDetailsTopBar(
	bowlerDetailsState: BowlerDetailsUiState,
	onAddLeague: () -> Unit,
	onBackPressed: () -> Unit,
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = when (bowlerDetailsState) {
					BowlerDetailsUiState.Loading -> ""
					is BowlerDetailsUiState.Success -> bowlerDetailsState.details.name
				},
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge
			)
		},
		navigationIcon = {
			IconButton(onClick = onBackPressed) {
				Icon(
					Icons.Default.ArrowBack,
					contentDescription = stringResource(R.string.cd_back),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
		actions = {
			IconButton(onClick = onAddLeague) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.league_list_add)
				)
			}
		},
	)
}