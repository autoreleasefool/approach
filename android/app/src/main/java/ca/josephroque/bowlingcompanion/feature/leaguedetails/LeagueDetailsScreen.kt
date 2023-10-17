package ca.josephroque.bowlingcompanion.feature.leaguedetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import ca.josephroque.bowlingcompanion.core.components.BackButton
import ca.josephroque.bowlingcompanion.feature.serieslist.SeriesListUiState
import ca.josephroque.bowlingcompanion.feature.serieslist.seriesList
import java.util.UUID

@Composable
internal fun LeagueDetailsRoute(
	onBackPressed: () -> Unit,
	onEditSeries: (UUID) -> Unit,
	onAddSeries: () -> Unit,
	onShowSeriesDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: LeagueDetailsViewModel = hiltViewModel(),
) {
	val leagueDetailsState by viewModel.leagueDetailsState.collectAsStateWithLifecycle()
	val seriesListState by viewModel.seriesListState.collectAsStateWithLifecycle()

	LeagueDetailsScreen(
		leagueDetailsState = leagueDetailsState,
		seriesListState = seriesListState,
		onBackPressed = onBackPressed,
		onSeriesClick = onShowSeriesDetails,
		onAddSeries = onAddSeries,
		modifier = modifier,
	)
}

@Composable
internal fun LeagueDetailsScreen(
	leagueDetailsState: LeagueDetailsUiState,
	seriesListState: SeriesListUiState,
	onBackPressed: () -> Unit,
	onSeriesClick: (UUID) -> Unit,
	onAddSeries: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			LeagueDetailsTopBar(
				leagueDetailsState = leagueDetailsState,
				onAddSeries = onAddSeries,
				onBackPressed = onBackPressed,
			)
		}
	) { padding ->
		LazyColumn(
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		) {
			item {
				Text(
					text = stringResource(R.string.series_list_title),
					style = MaterialTheme.typography.titleLarge,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
				)
			}

			seriesList(
				seriesListState = seriesListState,
				onSeriesClick = onSeriesClick,
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LeagueDetailsTopBar(
	leagueDetailsState: LeagueDetailsUiState,
	onAddSeries: () -> Unit,
	onBackPressed: () -> Unit
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = when (leagueDetailsState) {
					LeagueDetailsUiState.Loading -> ""
					is LeagueDetailsUiState.Success -> leagueDetailsState.details.name
				},
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = { BackButton(onClick = onBackPressed) },
		actions = {
			IconButton(onClick = onAddSeries) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.league_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}