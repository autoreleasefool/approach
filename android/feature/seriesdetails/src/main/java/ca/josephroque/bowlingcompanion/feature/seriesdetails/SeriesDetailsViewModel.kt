package ca.josephroque.bowlingcompanion.feature.seriesdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.GameID
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
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val seriesRepository: SeriesRepository,
	private val gamesRepository: GamesRepository,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<SeriesDetailsScreenEvent>() {
	private val seriesId = MutableStateFlow(Route.SeriesDetails.getSeries(savedStateHandle))
	private val eventId = Route.EventDetails.getEvent(savedStateHandle)

	private val gameToArchive: MutableStateFlow<GameListItem?> = MutableStateFlow(null)

	private val chartModelProducer = ChartEntryModelProducer()

	private val seriesDetails = seriesId
		.filterNotNull()
		.flatMapLatest { seriesRepository.getSeriesDetails(it) }

	private val gamesList = seriesId
		.filterNotNull()
		.flatMapLatest { gamesRepository.getGamesList(it) }

	val uiState: StateFlow<SeriesDetailsScreenUiState> = combine(
		gameToArchive,
		seriesDetails,
		gamesList,
	) { gameToArchive, seriesDetails, games ->
		val isShowingPlaceholder = seriesDetails.scores.all { it == 0 }
		chartModelProducer.setEntriesSuspending(
			if (isShowingPlaceholder) {
				listOf(
					entryOf(0f, 75f),
					entryOf(1f, 200f),
					entryOf(2f, 125f),
					entryOf(3f, 300f),
				)
			} else {
				seriesDetails.scores.mapIndexed { index, value ->
					entryOf(index.toFloat(), value.toFloat())
				}
			},
		).await()

		SeriesDetailsScreenUiState.Loaded(
			seriesDetails = SeriesDetailsUiState(
				details = seriesDetails.properties,
				scores = chartModelProducer,
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
		if (eventId != null) {
			viewModelScope.launch {
				val series = seriesRepository.getSeriesList(eventId, SeriesSortOrder.NEWEST_TO_OLDEST, null)
				seriesId.value = series.first().firstOrNull()?.properties?.id
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
			is GamesListUiAction.GameArchived -> gameToArchive.value = action.game
			is GamesListUiAction.ConfirmArchiveClicked -> archiveGame()
			is GamesListUiAction.DismissArchiveClicked -> gameToArchive.value = null
			is GamesListUiAction.GameClicked -> editGame(action.id)
		}
	}

	private fun editGame(id: GameID) {
		sendEvent(SeriesDetailsScreenEvent.EditGame(EditGameArgs(seriesId.value!!, id)))
		analyticsClient.startNewGameSession()
	}

	private fun addGameToSeries() {
		val seriesId = seriesId.value ?: return
		viewModelScope.launch {
			seriesRepository.addGameToSeries(seriesId)
		}
	}

	private fun archiveGame() {
		val gameToArchive = gameToArchive.value ?: return
		viewModelScope.launch {
			gamesRepository.archiveGame(gameToArchive.id)
			this@SeriesDetailsViewModel.gameToArchive.value = null
		}
	}
}
