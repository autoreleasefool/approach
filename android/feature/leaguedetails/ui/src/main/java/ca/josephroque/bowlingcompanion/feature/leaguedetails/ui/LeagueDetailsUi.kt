package ca.josephroque.bowlingcompanion.feature.leaguedetails.ui

import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListUiAction
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListUiState

data class LeagueDetailsTopBarUiState(
	val leagueName: String? = null,
	val isSortOrderMenuExpanded: Boolean = false,
	val sortOrder: SeriesSortOrder = SeriesSortOrder.NEWEST_TO_OLDEST,

)

data class LeagueDetailsUiState(
	val seriesList: SeriesListUiState,
	val topBar: LeagueDetailsTopBarUiState,
)

sealed interface LeagueDetailsUiAction {
	data object BackClicked : LeagueDetailsUiAction
	data object AddSeriesClicked : LeagueDetailsUiAction

	data object SortClicked: LeagueDetailsUiAction
	data object SortDismissed: LeagueDetailsUiAction
	data class SortOrderClicked(val sortOrder: SeriesSortOrder): LeagueDetailsUiAction

	data class SeriesList(val action: SeriesListUiAction) : LeagueDetailsUiAction
}