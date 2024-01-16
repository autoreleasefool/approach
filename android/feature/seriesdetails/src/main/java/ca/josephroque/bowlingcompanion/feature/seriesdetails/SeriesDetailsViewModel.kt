package ca.josephroque.bowlingcompanion.feature.seriesdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiAction
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiState
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val seriesRepository: SeriesRepository,
	private val gamesRepository: GamesRepository,
): ApproachViewModel<SeriesDetailsScreenEvent>() {
	private val _seriesId = MutableStateFlow(Route.SeriesDetails.getSeries(savedStateHandle))
	private val _eventId = Route.EventDetails.getEvent(savedStateHandle)

	private val _gameToArchive: MutableStateFlow<GameListItem?> = MutableStateFlow(null)

	private val _chartModelProducer = ChartEntryModelProducer()

	private val _seriesDetails = _seriesId
		.filterNotNull()
		.flatMapLatest { seriesRepository.getSeriesDetails(it) }

	private val _gamesList = _seriesId
		.filterNotNull()
		.flatMapLatest { gamesRepository.getGamesList(it) }

	val uiState: StateFlow<SeriesDetailsScreenUiState> = combine(
		_gameToArchive,
		_seriesDetails,
		_gamesList,
	) { gameToArchive, seriesDetails, games ->
		val isShowingPlaceholder = seriesDetails.scores.all { it == 0 }
		_chartModelProducer.setEntries(
			if (isShowingPlaceholder) {
				listOf(
					entryOf(0f, 75f),
					entryOf(1f, 200f),
					entryOf(2f, 125f),
					entryOf(3f, 300f),
				)
			} else {
				seriesDetails.scores.mapIndexed { index, value ->
					entryOf(
						index.toFloat(),
						value.toFloat()
					)
				}
			}
		)

		return@combine SeriesDetailsScreenUiState.Loaded(
			seriesDetails = SeriesDetailsUiState(
				details = seriesDetails.properties,
				scores = _chartModelProducer,
				seriesLow = seriesDetails.scores.minOrNull(),
				seriesHigh = seriesDetails.scores.maxOrNull(),
				isShowingPlaceholder = isShowingPlaceholder,
				gamesList = GamesListUiState(
					list = games,
					gameToArchive = gameToArchive,
				),
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = SeriesDetailsScreenUiState.Loading,
	)

	init {
		if (_eventId != null) {
			viewModelScope.launch {
				val series = seriesRepository.getSeriesList(_eventId, SeriesSortOrder.NEWEST_TO_OLDEST)
				_seriesId.value = series.first().firstOrNull()?.properties?.id
			}
		}
	}

	fun handleAction(action: SeriesDetailsScreenUiAction) {
		when (action) {
			is SeriesDetailsScreenUiAction.SeriesDetails -> handleSeriesDetailsAction(action.action)
		}
	}

	private fun handleSeriesDetailsAction(action: SeriesDetailsUiAction) {
		when (action) {
			is SeriesDetailsUiAction.BackClicked -> sendEvent(SeriesDetailsScreenEvent.Dismissed)
			is SeriesDetailsUiAction.AddGameClicked -> addGameToSeries()
			is SeriesDetailsUiAction.GamesList -> handleGamesListAction(action.action)
		}
	}

	private fun handleGamesListAction(action: GamesListUiAction) {
		when (action) {
			GamesListUiAction.AddGameClicked -> addGameToSeries()
			is GamesListUiAction.GameArchived -> _gameToArchive.value = action.game
			is GamesListUiAction.ConfirmArchiveClicked -> archiveGame()
			is GamesListUiAction.DismissArchiveClicked -> _gameToArchive.value = null
			is GamesListUiAction.GameClicked -> sendEvent(SeriesDetailsScreenEvent.EditGame(EditGameArgs(_seriesId.value!!, action.id)))
		}
	}

	private fun addGameToSeries() {
		val seriesId = _seriesId.value ?: return
		viewModelScope.launch {
			seriesRepository.addGameToSeries(seriesId)
		}
	}

	private fun archiveGame() {
		val gameToArchive = _gameToArchive.value ?: return
		viewModelScope.launch {
			gamesRepository.archiveGame(gameToArchive.id)
			_gameToArchive.value = null
		}
	}
}
