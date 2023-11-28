package ca.josephroque.bowlingcompanion.feature.seriesdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiAction
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiState
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.SERIES_ID
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsUiState
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
class SeriesDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	seriesRepository: SeriesRepository,
	private val gamesRepository: GamesRepository,
): ApproachViewModel<SeriesDetailsScreenEvent>() {
	private val seriesId = UUID.fromString(savedStateHandle[SERIES_ID])

	private val _gameToArchive: MutableStateFlow<GameListItem?> = MutableStateFlow(null)

	val uiState: StateFlow<SeriesDetailsScreenUiState> = combine(
		_gameToArchive,
		seriesRepository.getSeriesDetails(seriesId),
		gamesRepository.getGamesList(seriesId),
	) { gameToArchive, seriesDetails, games ->
		SeriesDetailsScreenUiState.Loaded(
			seriesDetails = SeriesDetailsUiState(
				details = seriesDetails.properties,
				scores = ChartEntryModelProducer(
					seriesDetails.scores.mapIndexed { index, value -> entryOf(index.toFloat(), value.toFloat()) }
				).getModel(),
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
			is GamesListUiAction.GameClicked -> sendEvent(SeriesDetailsScreenEvent.EditGame(EditGameArgs(seriesId, action.id)))
		}
	}

	private fun addGameToSeries() {
		TODO()
	}

	private fun archiveGame() {
		val gameToArchive = _gameToArchive.value ?: return
		viewModelScope.launch {
			gamesRepository.archiveGame(gameToArchive.id)
			_gameToArchive.value = null
		}
	}
}
