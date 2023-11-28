package ca.josephroque.bowlingcompanion.feature.leaguedetails.ui

import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListUiAction
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListUiState

data class LeagueDetailsUiState(
	val leagueName: String,
	val seriesList: SeriesListUiState,
)

sealed interface LeagueDetailsUiAction {
	data object BackClicked : LeagueDetailsUiAction
	data object AddSeriesClicked : LeagueDetailsUiAction

	data class SeriesList(val action: SeriesListUiAction) : LeagueDetailsUiAction
}