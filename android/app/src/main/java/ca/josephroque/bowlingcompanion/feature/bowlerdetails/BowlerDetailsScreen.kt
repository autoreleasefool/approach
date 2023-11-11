package ca.josephroque.bowlingcompanion.feature.bowlerdetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
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
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.HeaderAction
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.footer
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.gearList
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
	onShowGearDetails: (UUID) -> Unit,
	onShowPreferredGearPicker: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: BowlerDetailsViewModel = hiltViewModel(),
) {
	val bowlerDetailsState by viewModel.bowlerDetailsState.collectAsStateWithLifecycle()
	val leaguesListState by viewModel.leaguesListState.collectAsStateWithLifecycle()
	val gearListState by viewModel.gearListState.collectAsStateWithLifecycle()

	BowlerDetailsScreen(
		bowlerDetailsState = bowlerDetailsState,
		leaguesListState = leaguesListState,
		gearListState = gearListState,
		onBackPressed = onBackPressed,
		onLeagueClick = onShowLeagueDetails,
		onGearClick = onShowGearDetails,
		onManagePreferredGearClick = onShowPreferredGearPicker,
		onAddLeague = { bowlerDetailsState.bowlerId()?.let { onAddLeague(it) } },
		editStatisticsWidget = viewModel::editStatisticsWidget,
		modifier = modifier,
	)
}

@Composable
internal fun BowlerDetailsScreen(
	bowlerDetailsState: BowlerDetailsUiState,
	leaguesListState: LeaguesListUiState,
	gearListState: GearListUiState?,
	onBackPressed: () -> Unit,
	onGearClick: (UUID) -> Unit,
	onLeagueClick: (UUID) -> Unit,
	onManagePreferredGearClick: () -> Unit,
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

			header(R.string.league_list_title)

			leaguesList(
				leaguesListState = leaguesListState,
				onLeagueClick = onLeagueClick,
			)

			item {
				Divider(modifier = Modifier.padding(bottom = 8.dp))
			}

			header(
				titleResourceId = R.string.bowler_details_preferred_gear_title,
				action = HeaderAction(
					actionResourceId = RCoreDesign.string.action_manage,
					onClick = onManagePreferredGearClick,
				),
			)

			if (gearListState != null) {
				gearList(
					list = gearListState.list,
					onGearClick = { onGearClick(it.id) },
				)
			}

			footer(R.string.bowler_details_preferred_gear_description)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BowlerDetailsTopBar(
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
		navigationIcon = { BackButton(onClick = onBackPressed) },
		actions = {
			IconButton(onClick = onAddLeague) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.league_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}