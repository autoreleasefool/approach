package ca.josephroque.bowlingcompanion.feature.leaguedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.LEAGUE_ID
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsUiState
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListChartItem
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListUiAction
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LeagueDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	leaguesRepository: LeaguesRepository,
	private val seriesRepository: SeriesRepository,
): ApproachViewModel<LeagueDetailsScreenEvent>() {
	private val leagueId = UUID.fromString(savedStateHandle[LEAGUE_ID])

	private val _seriesToArchive: MutableStateFlow<SeriesListChartItem?> = MutableStateFlow(null)

	val uiState: StateFlow<LeagueDetailsScreenUiState> = combine(
		_seriesToArchive,
		leaguesRepository.getLeagueDetails(leagueId),
		seriesRepository.getSeriesList(leagueId),
	) { seriesToArchive, league, series ->
		LeagueDetailsScreenUiState.Loaded(
			leagueDetails = LeagueDetailsUiState(
				leagueName = league.name,
				seriesList = SeriesListUiState(
					list = series.map(SeriesListItem::withChart),
					seriesToArchive = seriesToArchive,
				)
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = LeagueDetailsScreenUiState.Loading,
	)

	fun handleAction(action: LeagueDetailsScreenUiAction) {
		when (action) {
			is LeagueDetailsScreenUiAction.LeagueDetails -> handleLeagueDetailsAction(action.action)
		}
	}

	private fun handleLeagueDetailsAction(action: LeagueDetailsUiAction) {
		when (action) {
			LeagueDetailsUiAction.BackClicked -> sendEvent(LeagueDetailsScreenEvent.Dismissed)
			LeagueDetailsUiAction.AddSeriesClicked -> sendEvent(LeagueDetailsScreenEvent.AddSeries(leagueId))
			is LeagueDetailsUiAction.SeriesList -> handleSeriesListAction(action.action)
		}
	}

	private fun handleSeriesListAction(action: SeriesListUiAction) {
		when (action) {
			is SeriesListUiAction.SeriesClicked -> sendEvent(LeagueDetailsScreenEvent.ShowSeriesDetails(action.id))
			is SeriesListUiAction.EditSeriesClicked -> sendEvent(LeagueDetailsScreenEvent.EditSeries(action.id))
			SeriesListUiAction.AddSeriesClicked -> sendEvent(LeagueDetailsScreenEvent.AddSeries(leagueId))
			is SeriesListUiAction.ArchiveSeriesClicked -> _seriesToArchive.value = action.series
			SeriesListUiAction.ConfirmArchiveClicked -> archiveSeries()
			SeriesListUiAction.DismissArchiveClicked -> _seriesToArchive.value = null
		}
	}

	private fun archiveSeries() {
		val seriesToArchive = _seriesToArchive.value ?: return
		viewModelScope.launch {
			seriesRepository.archiveSeries(seriesToArchive.id)
			_seriesToArchive.value = null
		}
	}
}

private fun SeriesListItem.withChart(): SeriesListChartItem = SeriesListChartItem(
	id = properties.id,
	date = properties.date,
	preBowl = properties.preBowl,
	total = properties.total,
	numberOfGames = scores.size,
	scores = ChartEntryModelProducer(
		scores.mapIndexed { index, value -> entryOf(index.toFloat(), value.toFloat()) }
	).getModel()
)
