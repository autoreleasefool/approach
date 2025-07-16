package ca.josephroque.bowlingcompanion.feature.seriesdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiAction
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiState
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsUiState
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val seriesRepository: SeriesRepository,
	private val gamesRepository: GamesRepository,
	private val analyticsClient: AnalyticsClient,
	private val featureFlags: FeatureFlagsClient,
) : ApproachViewModel<SeriesDetailsScreenEvent>() {
	private val seriesId = MutableStateFlow(Route.SeriesDetails.getSeries(savedStateHandle))
	private val eventId = Route.EventDetails.getEvent(savedStateHandle)
	private val sharingSource = MutableStateFlow<SharingSource?>(null)

	private val gameToArchive: MutableStateFlow<GameListItem?> = MutableStateFlow(null)
	private val isReorderingGames: MutableStateFlow<Boolean> = MutableStateFlow(false)

	private val chartModelProducer = ChartEntryModelProducer()

	private val seriesDetails = seriesId
		.filterNotNull()
		.flatMapLatest { seriesRepository.getSeriesDetails(it) }

	private val seriesGamesList = seriesId
		.filterNotNull()
		.flatMapLatest { gamesRepository.getGamesList(it) }
	private val reorderedGames: MutableStateFlow<List<GameListItem>> = MutableStateFlow(emptyList())

	private val gamesList = combine(
		seriesGamesList,
		reorderedGames,
		isReorderingGames,
	) { seriesGamesList, reorderedGames, isReorderingGames ->
		if (isReorderingGames) reorderedGames else seriesGamesList
	}

	private val topBarState = seriesDetails
		.filterNotNull()
		.combine(isReorderingGames) { seriesDetails, isReorderingGames ->
			SeriesDetailsTopBarUiState(
				seriesDate = seriesDetails.properties.appliedDate ?: seriesDetails.properties.date,
				isSharingButtonVisible = featureFlags.isEnabled(FeatureFlag.SHARING_SERIES),
				isReorderGamesButtonVisible = featureFlags.isEnabled(FeatureFlag.REORDER_GAMES),
				isReorderingGames = isReorderingGames,
			)
		}

	private val gamesListState = combine(
		gameToArchive,
		gamesList,
		isReorderingGames,
	) { gameToArchive, gamesList, isReorderingGames ->
		GamesListUiState(
			gameToArchive = gameToArchive,
			list = gamesList,
			isReordering = isReorderingGames,
		)
	}

	private val seriesDetailsState = combine(
		seriesDetails,
		gamesListState,
		sharingSource,
	) { seriesDetails, gamesList, sharingSource ->
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

		SeriesDetailsUiState(
			details = seriesDetails.properties,
			scores = chartModelProducer,
			seriesLow = seriesDetails.scores.minOrNull(),
			seriesHigh = seriesDetails.scores.maxOrNull(),
			isShowingPlaceholder = isShowingPlaceholder,
			gamesList = gamesList,
			sharingSeries = sharingSource,
		)
	}

	val uiState: StateFlow<SeriesDetailsScreenUiState> = combine(
		topBarState,
		seriesDetailsState,
	) { topBarState, seriesDetailsState ->
		SeriesDetailsScreenUiState.Loaded(
			topBar = topBarState,
			seriesDetails = seriesDetailsState,
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
			SeriesDetailsScreenUiAction.SharingDismissed -> dismissSharing()
			is SeriesDetailsScreenUiAction.SeriesDetails -> handleSeriesDetailsAction(action.action)
			is SeriesDetailsScreenUiAction.TopBar -> handleTopBarAction(action.action)
		}
	}

	private fun handleTopBarAction(action: SeriesDetailsTopBarUiAction) {
		when (action) {
			is SeriesDetailsTopBarUiAction.BackClicked -> sendEvent(SeriesDetailsScreenEvent.Dismissed)
			is SeriesDetailsTopBarUiAction.AddGameClicked -> addGameToSeries()
			is SeriesDetailsTopBarUiAction.ShareClicked -> shareSeries()
			is SeriesDetailsTopBarUiAction.CancelReorderClicked -> finishReorderingGames(isCancelled = true)
			is SeriesDetailsTopBarUiAction.ConfirmReorderClicked -> finishReorderingGames(isCancelled = false)
			is SeriesDetailsTopBarUiAction.ReorderGamesClicked -> startReorderingGames()
		}
	}

	private fun handleSeriesDetailsAction(action: SeriesDetailsUiAction) {
		when (action) {
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
			is GamesListUiAction.GameMoved -> moveGame(action.from, action.to, action.offset)
		}
	}

	private fun startReorderingGames() {
		viewModelScope.launch {
			reorderedGames.value = seriesGamesList.first()
			isReorderingGames.value = true
		}
	}

	private fun finishReorderingGames(isCancelled: Boolean) {
		viewModelScope.launch {
			isReorderingGames.value = false
			if (!isCancelled) {
				val seriesId = seriesId.value ?: return@launch
				val orderedGames = reorderedGames.value
				if (!orderedGames.isEmpty()) {
					viewModelScope.launch {
						gamesRepository.setGameOrder(seriesId, orderedGames.map(GameListItem::id))
					}
				}
			}
		}
	}

	private fun editGame(id: GameID) {
		sendEvent(SeriesDetailsScreenEvent.EditGame(EditGameArgs(seriesId.value!!, id)))
		analyticsClient.startNewGameSession()
	}

	private fun shareSeries() {
		val seriesId = seriesId.value ?: return
		sharingSource.value = SharingSource.Series(seriesId)
	}

	private fun dismissSharing() {
		sharingSource.value = null
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

	private fun moveGame(fromListIndex: Int, toListIndex: Int, indexOffset: Int) {
		viewModelScope.launch {
			val from = fromListIndex - indexOffset
			val to = toListIndex - indexOffset
			reorderedGames.update {
				if (from == to || !it.indices.contains(from) || !it.indices.contains(to)) return@update it
				it.toMutableList().apply { add(to, removeAt(from)) }
			}
		}
	}
}
