package ca.josephroque.bowlingcompanion.feature.leaguedetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.feature.serieslist.SeriesListUiState
import ca.josephroque.bowlingcompanion.feature.serieslist.seriesList
import java.util.UUID

@Composable
internal fun LeagueDetailsRoute(
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
		onSeriesClick = onShowSeriesDetails,
		onAddSeries = onAddSeries,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LeagueDetailsScreen(
	leagueDetailsState: LeagueDetailsUiState,
	seriesListState: SeriesListUiState,
	onSeriesClick: (UUID) -> Unit,
	onAddSeries: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
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
					)
				},
				actions = {
					IconButton(onClick = onAddSeries) {
						Icon(
							imageVector = Icons.Filled.Add,
							contentDescription = stringResource(R.string.league_list_add)
						)
					}
				},
			)
		}
	) { padding ->
		LazyColumn(
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		) {
			seriesList(
				seriesListState = seriesListState,
				onSeriesClick = onSeriesClick,
			)
		}
	}
}