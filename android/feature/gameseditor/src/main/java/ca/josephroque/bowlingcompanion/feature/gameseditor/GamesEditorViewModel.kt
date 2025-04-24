package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.game.GameManualScoreSet
import ca.josephroque.bowlingcompanion.core.analytics.trackable.game.GameUpdated
import ca.josephroque.bowlingcompanion.core.analytics.trackable.game.GameViewed
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.FramesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.MatchPlaysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.ScoresRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamSeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.Frame
import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.ScoringGame
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesUpdate
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.nextIndexToRecord
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetListUiState
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.AnimationDirection
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.NextGameEditableElement
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.lanes.CopyLanesDialogUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.lanes.CopyLanesDialogUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.GameLoadDate
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.ensureRollExists
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.selectedFrame
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.setBallRolled
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.setDidFoul
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.setPinsDowned
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateFrameEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateGameDetails
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateGameDetailsAndGet
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateGameScore
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateGamesEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateGamesEditorAndGet
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateHeader
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateSelection
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

val GAMES_EDITOR_GAME_GEAR_RESULT_KEY =
	ResourcePickerResultKey("GamesEditorGameGearResultKey")
val GAMES_EDITOR_GAME_ROLLED_BALL_RESULT_KEY =
	ResourcePickerResultKey("GamesEditorGameRolledBallResultKey")
val GAMES_EDITOR_GAME_ALLEY_RESULT_KEY =
	ResourcePickerResultKey("GamesEditorGameAlleyResultKey")
val GAMES_EDITOR_GAME_LANES_RESULT_KEY =
	ResourcePickerResultKey("GamesEditorGameLanesResultKey")

@HiltViewModel
class GamesEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository,
	private val framesRepository: FramesRepository,
	private val gamesRepository: GamesRepository,
	private val gearRepository: GearRepository,
	private val matchPlaysRepository: MatchPlaysRepository,
	private val scoresRepository: ScoresRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	private val alleysRepository: AlleysRepository,
	private val lanesRepository: LanesRepository,
	private val seriesRepository: SeriesRepository,
	private val teamSeriesRepository: TeamSeriesRepository,
	private val analyticsClient: AnalyticsClient,
	private val userDataRepository: UserDataRepository,
	private val featureFlagsClient: FeatureFlagsClient,
	@ApplicationScope private val scope: CoroutineScope,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : ApproachViewModel<GamesEditorScreenEvent>(),
	DefaultLifecycleObserver {
	private val teamSeriesId = Route.EditTeamSeries.getTeamSeries(savedStateHandle)
	private val series = MutableStateFlow(
		if (teamSeriesId != null) {
			emptyList()
		} else {
			Route.EditGame.getSeries(savedStateHandle)
		},
	)

	private val initialGameId = if (teamSeriesId != null) {
		Route.EditTeamSeries.getGame(savedStateHandle)!!
	} else {
		Route.EditGame.getGame(savedStateHandle)!!
	}

	private val sharingSource: MutableStateFlow<SharingSource?> = MutableStateFlow(null)

	private val bowlers = series.flatMapLatest { bowlersRepository.getSeriesBowlers(it) }
	private val currentBowlerId = MutableStateFlow<BowlerID?>(null)

	private var initialGameLoaded = false
	private val currentGameId = MutableStateFlow(initialGameId)
	private val headerPeekHeight = MutableStateFlow(0f)

	private val isGameDetailsSheetVisible = MutableStateFlow(true)
	private val isGameLockSnackBarVisible = MutableStateFlow(false)
	private val highestScorePossibleAlert = MutableStateFlow<HighestScorePossibleAlertUiState?>(null)

	private val lastLoadedGameAt = MutableStateFlow<GameLoadDate?>(null)
	private var lastUpdatedDurationAtMillis: Long = 0

	private var ballsJob: Job? = null
	private var framesJob: Job? = null
	private var scoresJob: Job? = null
	private val gamesEditorState = MutableStateFlow(GamesEditorUiState(gameId = initialGameId))

	private var alleyJob: Job? = null
	private var lanesJob: Job? = null
	private var gearJob: Job? = null
	private var matchPlayJob: Job? = null
	private var seriesDetailsJob: Job? = null
	private var bowlerDetailsJob: Job? = null
	private var gameDetailsJob: Job? = null
	private val gameDetailsState = MutableStateFlow(GameDetailsUiState(gameId = initialGameId))
	private val currentGameIndex = gameDetailsState.map { it.currentGameIndex }

	private suspend fun isShowingTeamScores(): Boolean =
		!userDataRepository.userData.first().isHidingTeamScoresInGameDetails
	private val isShowingTeamScores = userDataRepository.userData.map {
		!it.isHidingTeamScoresInGameDetails
	}

	private val topBarState = currentGameIndex.map {
		GamesEditorTopBarUiState(
			currentGameIndex = it,
			isSharingButtonVisible = featureFlagsClient.isEnabled(FeatureFlag.SHARING_GAMES),
		)
	}

	private val bottomSheetAppearanceUiState: Flow<GamesEditorScreenBottomSheetAppearanceUiState> = combine(
		headerPeekHeight,
		isGameDetailsSheetVisible,
	) {
			headerPeekHeight,
			isGameDetailsSheetVisible,
		->
		GamesEditorScreenBottomSheetAppearanceUiState(
			headerPeekHeight = headerPeekHeight,
			isGameDetailsSheetVisible = isGameDetailsSheetVisible,
		)
	}

	private val bottomSheetContentUiState: Flow<GamesEditorScreenBottomSheetContentUiState> = combine(
		gameDetailsState,
		bottomSheetAppearanceUiState,
	) {
			gameDetails,
			appearance,
		->
		GamesEditorScreenBottomSheetContentUiState(
			gameDetails = gameDetails,
			appearance = appearance,
		)
	}

	private val alertsUiState: Flow<GamesEditorScreenAlertsUiState> = combine(
		isGameLockSnackBarVisible,
		highestScorePossibleAlert,
	) {
			isGameLockSnackBarVisible,
			highestScorePossibleAlert,
		->
		GamesEditorScreenAlertsUiState(
			isGameLockSnackBarVisible = isGameLockSnackBarVisible,
			highestScorePossibleAlert = highestScorePossibleAlert,
		)
	}

	private val contentUiState: Flow<GamesEditorScreenContentUiState> = combine(
		gamesEditorState,
		topBarState,
	) {
			gamesEditor,
			topBar,
		->
		GamesEditorScreenContentUiState(
			gamesEditor = gamesEditor,
			topBar = topBar,
		)
	}

	val uiState: StateFlow<GamesEditorScreenUiState> = combine(
		alertsUiState,
		bottomSheetContentUiState,
		contentUiState,
		sharingSource,
	) {
			alertsUiState,
			bottomSheetContentUiState,
			contentUiState,
			sharingSource,
		->
		GamesEditorScreenUiState.Loaded(
			alerts = alertsUiState,
			content = contentUiState,
			bottomSheet = bottomSheetContentUiState,
			sharingSource = sharingSource,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = GamesEditorScreenUiState.Loading,
	)

	init {
		viewModelScope.launch(ioDispatcher) {
			currentGameId
				.onEach { Route.EditGame.setGame(savedStateHandle, it) }
				.launchIn(this)
		}
	}

	override fun onResume(owner: LifecycleOwner) {
		isGameDetailsSheetVisible.value = true
	}

	fun handleAction(action: GamesEditorScreenUiAction) {
		when (action) {
			GamesEditorScreenUiAction.DidAppear -> loadInitialData()
			GamesEditorScreenUiAction.DidDisappear -> dismissLatestGameInEditor()
			GamesEditorScreenUiAction.GameLockSnackBarDismissed -> dismissGameLockSnackBar()
			GamesEditorScreenUiAction.GameLockSnackBarUnlocked -> {
				dismissGameLockSnackBar()
				toggleGameLocked(isLocked = false)
			}
			GamesEditorScreenUiAction.HighestPossibleScoreSnackBarDismissed ->
				dismissHighestPossibleScoreSnackBar()
			GamesEditorScreenUiAction.SharingDismissed -> dismissShareSheet()
			is GamesEditorScreenUiAction.TopBar -> handleTopBarAction(action.action)
			is GamesEditorScreenUiAction.GamesEditor -> handleGamesEditorAction(action.action)
			is GamesEditorScreenUiAction.GameDetails -> handleGameDetailsAction(action.action)
			is GamesEditorScreenUiAction.GearUpdated -> updateGear(action.gearIds)
			is GamesEditorScreenUiAction.AlleyUpdated -> updateAlley(action.alleyId)
			is GamesEditorScreenUiAction.LanesUpdated -> updateLanes(action.laneIds)
			is GamesEditorScreenUiAction.SeriesUpdated -> updateSeries(action.series)
			is GamesEditorScreenUiAction.CurrentGameUpdated -> loadGameIfChanged(action.gameId)
			is GamesEditorScreenUiAction.SelectedBallUpdated -> updateSelectedBall(id = action.ballId)
			is GamesEditorScreenUiAction.ScoreUpdated -> updateScore(action.score, action.scoringMethod)
		}
	}

	private fun handleTopBarAction(action: GamesEditorTopBarUiAction) {
		when (action) {
			GamesEditorTopBarUiAction.BackClicked -> dismiss()
			GamesEditorTopBarUiAction.ShareClicked -> openShareSheet()
			GamesEditorTopBarUiAction.SettingsClicked -> openGameSettings()
		}
	}

	private fun handleGameDetailsAction(action: GameDetailsUiAction) {
		when (action) {
			GameDetailsUiAction.ManageGearClicked -> openGearPicker()
			GameDetailsUiAction.ManageMatchPlayClicked -> openMatchPlayManager()
			GameDetailsUiAction.ManageScoreClicked -> openScoreSettings()
			GameDetailsUiAction.ViewGameStatsClicked -> openGameStats()
			GameDetailsUiAction.ViewSeriesStatsClicked -> openSeriesStats()
			GameDetailsUiAction.ManageLanesClicked -> openLanesPicker()
			GameDetailsUiAction.ManageAlleyClicked -> openAlleyPicker()
			GameDetailsUiAction.ViewAllBowlersClicked -> openAllBowlersScores()
			GameDetailsUiAction.ShowHighestPossibleScoreClicked -> calculateHighestPossibleScore()
			is GameDetailsUiAction.LockToggled -> toggleGameLocked(action.locked)
			is GameDetailsUiAction.ExcludeFromStatisticsToggled -> toggleGameExcludedFromStatistics(
				action.excludeFromStatistics,
			)
			is GameDetailsUiAction.NextGameElementClicked -> goToNext(action.nextGameElement)
			is GameDetailsUiAction.HeaderHeightMeasured -> setHeaderPeekHeight(action.height)
			is GameDetailsUiAction.CopyLanesDialog -> handleCopyLanesAction(action.action)
			is GameDetailsUiAction.ScoreSheetList -> handleScoreSheetListAction(action.action)
		}
	}

	private fun handleCopyLanesAction(action: CopyLanesDialogUiAction) {
		when (action) {
			CopyLanesDialogUiAction.Dismissed -> setCopyLanesDialog(isVisible = false)
			CopyLanesDialogUiAction.CopyToAllClicked -> copyLanesToAllGames()
		}
	}

	private fun handleGamesEditorAction(action: GamesEditorUiAction) {
		when (action) {
			GamesEditorUiAction.ManualScoreClicked -> openScoreSettings()
			is GamesEditorUiAction.FrameEditor -> handleFrameEditorAction(action.action)
			is GamesEditorUiAction.RollEditor -> handleRollEditorAction(action.action)
			is GamesEditorUiAction.ScoreSheet -> handleScoreSheetAction(action.action)
		}
	}

	private fun handleFrameEditorAction(action: FrameEditorUiAction) {
		when (action) {
			FrameEditorUiAction.FrameEditorInteractionStarted -> handleInteractionWithPins()
			FrameEditorUiAction.AnimationFinished -> setFrameEditorAnimation(null)
			FrameEditorUiAction.DragHintDismissed -> dismissDragHint()
			is FrameEditorUiAction.DownedPinsChanged -> updateDownedPins(action.downedPins)
		}
	}

	private fun handleRollEditorAction(action: RollEditorUiAction) {
		when (action) {
			RollEditorUiAction.PickBallClicked -> openBallRolledPicker()
			is RollEditorUiAction.BallClicked -> updateSelectedBall(action.ball)
			is RollEditorUiAction.FoulToggled -> toggleDidFoul(action.foul)
		}
	}

	private fun handleScoreSheetAction(action: ScoreSheetUiAction) {
		when (action) {
			is ScoreSheetUiAction.RollClicked -> updateSelectedRoll(action.frameIndex, action.rollIndex)
			is ScoreSheetUiAction.FrameClicked -> updateSelectedFrame(action.frameIndex)
		}
	}

	private fun handleScoreSheetListAction(action: ScoreSheetUiAction) {
		// Intentionally do nothing when items in the score sheet list are clicked
		when (action) {
			is ScoreSheetUiAction.RollClicked -> Unit
			is ScoreSheetUiAction.FrameClicked -> Unit
		}
	}

	private fun calculateHighestPossibleScore() {
		viewModelScope.launch(ioDispatcher) {
			val gameId = currentGameId.value
			val highestScorePossible = scoresRepository.getHighestScorePossible(gameId)
			highestScorePossibleAlert.value = HighestScorePossibleAlertUiState(score = highestScorePossible)
		}
	}

	private fun updateSeries(series: List<SeriesID>) {
		this.series.update { series }
		if (teamSeriesId != null) {
			viewModelScope.launch(ioDispatcher) {
				teamSeriesRepository.updateTeamSeries(
					TeamSeriesUpdate(
						id = teamSeriesId,
						seriesIds = series,
					),
				)
			}
		}
	}

	private fun loadGameIfChanged(gameId: GameID) {
		if (currentGameId.value == gameId) return
		loadGame(gameId)
	}

	private fun loadInitialData() {
		loadTeamSeries()
		loadInitialGame()
		loadSeriesScores()
	}

	private fun loadTeamSeries() {
		if (teamSeriesId == null || series.value.isNotEmpty()) return
		viewModelScope.launch(ioDispatcher) {
			val teamSeries = seriesRepository.getTeamSeriesIds(teamSeriesId).first()
			series.update { teamSeries }
		}
	}

	private fun loadInitialGame() {
		if (initialGameLoaded) return
		initialGameLoaded = true
		loadGame(initialGameId)
	}

	private fun loadSeriesScores() {
		scoresJob?.cancel()
		scoresJob = viewModelScope.launch(ioDispatcher) {
			isShowingTeamScores
				.onEach { isShowingTeamScores ->
					if (!isShowingTeamScores) {
						gameDetailsState.update {
							it.copy(
								scoresList = null,
							)
						}
					}
				}
				.launchIn(this)

			combine(
				isShowingTeamScores,
				currentBowlerId,
			) { isShowingTeamScores, currentBowlerId ->
				if (!isShowingTeamScores) null else currentBowlerId
			}
				.filterNotNull()
				.onEach { currentBowlerId ->
					val gameDetails = gameDetailsState.updateAndGet {
						it.copy(
							scoresList = it.scoresList?.copy(
								highlightedGame = ScoreSheetListUiState.HighlightedGame(
									bowlerId = currentBowlerId,
									// Set to zero because `ScoreSheetList` concept of gameIndex is
									// relative to the number of series, and we only pass 1 series in this view
									gameIndex = 0,
								),
							),
						)
					}

					gamesEditorState.updateGamesEditor(currentGameId.value) {
						it.copy(
							scoreSheet = it.scoreSheet.copy(
								game = gameDetails.scoresList
									?.bowlerScores
									?.firstOrNull()
									?.firstOrNull { score -> score.bowler.id == currentBowlerId }
									?.scoreSheet?.game
									?: it.scoreSheet.game,
							),
						)
					}
				}
				.launchIn(this)

			var scoreJobs = mutableListOf<Job>()
			combine(
				series.filter { it.isNotEmpty() },
				currentGameIndex.distinctUntilChanged(),
			) { series, currentGameIndex ->
				val games = gamesRepository.getGamesFromSeries(series, currentGameIndex).first()
				games.map { game -> gamesRepository.getGameDetails(game.id).first() }
			}
				.onEach {
					scoreJobs.forEach { it.cancel() }
					scoreJobs = mutableListOf()
				}
				.onEach { gamesDetails ->
					val gameOrder = gamesDetails.mapIndexed { index, gameDetails ->
						gameDetails.properties.id to index
					}.toMap()

					gamesDetails.forEach { gameDetails ->
						scoreJobs.add(
							this.launch { loadScoreForGame(gameDetails, gameOrder) },
						)
					}
				}
				.launchIn(this)
		}
	}

	private suspend fun loadScoreForGame(gameDetails: GameEdit, gameOrder: Map<GameID, Int>) {
		scoresRepository.getScore(gameDetails.properties.id).collect { score ->
			if (score.id == currentGameId.value) {
				updateScoreForCurrentGame(score)
			}

			if (!isShowingTeamScores()) return@collect

			// We don't use `updateGameDetails` here because we want to update the state
			// regardless of the current game ID
			gameDetailsState.update {
				it.updateGameScore(
					currentBowlerId = currentBowlerId.first(),
					score = score,
					gameDetails = gameDetails,
					gameOrder = gameOrder,
				)
			}
		}
	}

	private suspend fun updateScoreForCurrentGame(score: ScoringGame) {
		val gameId = score.id

		gamesEditorState.updateGamesEditor(gameId) {
			it.copy(
				scoreSheet = it.scoreSheet.copy(
					game = score,
				),
			)
		}

		val originalGameDetails = gameDetailsState.value
		val gameDetails = gameDetailsState.updateGameDetailsAndGet(gameId) {
			when (it.scoringMethod.scoringMethod) {
				GameScoringMethod.MANUAL -> it
				GameScoringMethod.BY_FRAME -> it.copy(
					scoringMethod = it.scoringMethod.copy(score = score.score ?: 0),
				)
			}
		}

		// Only save details back to the game if they've changed, to avoid an infinite update loop
		if (
			originalGameDetails.gameId == gameDetails.gameId &&
			originalGameDetails.scoringMethod != gameDetails.scoringMethod
		) {
			when (gameDetails.scoringMethod.scoringMethod) {
				GameScoringMethod.MANUAL -> Unit
				GameScoringMethod.BY_FRAME -> viewModelScope.launch(ioDispatcher) {
					gamesRepository.setGameScore(
						gameDetails.gameId,
						gameDetails.scoringMethod.score,
					)
				}
			}
		}

		// Only update the game duration if there is a greater than 2 second diff, to avoid an infinite loop
		val lastLoadedGameAt = this@GamesEditorViewModel.lastLoadedGameAt.value
		val nextValidUpdateAt = lastUpdatedDurationAtMillis + 2_000
		if (lastLoadedGameAt?.gameId == gameDetails.gameId &&
			nextValidUpdateAt < System.currentTimeMillis()
		) {
			lastUpdatedDurationAtMillis = System.currentTimeMillis()
			val durationMillis = System.currentTimeMillis() - lastLoadedGameAt.loadedAt
			gamesRepository.setGameDuration(
				gameDetails.gameId,
				lastLoadedGameAt.durationMillisWhenLoaded + durationMillis,
			)
		}
	}

	private fun loadBowlerGame(bowlerId: BowlerID, gameIndex: Int? = null) {
		viewModelScope.launch(ioDispatcher) {
			val gameIndexToLoad = gameIndex ?: gameDetailsState.value.currentGameIndex
			val bowlerSeriesId = getBowlerSeriesId(bowlerId)
			val seriesGames = gamesRepository.getGamesList(bowlerSeriesId).first()
			val gameToLoad = seriesGames[gameIndexToLoad].id

			currentBowlerId.update { bowlerId }
			loadGame(gameToLoad)
		}
	}

	private fun loadGame(gameId: GameID) {
		val gameToLoad = currentGameId.updateAndGet { gameId }
		gameDetailsState.update { it.copy(gameId = gameToLoad) }
		gamesEditorState.update { it.copy(gameId = gameToLoad) }

		analyticsClient.trackEvent(GameViewed(gameId = gameId))
		setLatestGameId(gameId)

		viewModelScope.launch(ioDispatcher) {
			userDataRepository.userData.collect { userData ->
				gamesEditorState.update {
					it.copy(
						frameEditor = it.frameEditor.copy(
							isShowingDragHint = !userData.isFrameDragHintDismissed,
						),
					)
				}
			}
		}

		gameDetailsJob?.cancel()
		gameDetailsJob = viewModelScope.launch(ioDispatcher) {
			gamesRepository.getGameDetails(gameToLoad).collect { gameDetails ->
				currentBowlerId.update { gameDetails.bowler.id }

				gameDetailsState.updateGameDetails(gameToLoad) {
					it.copy(
						gameId = gameDetails.properties.id,
						currentGameIndex = gameDetails.properties.index,
						header = it.header.copy(
							bowlerName = gameDetails.bowler.name,
							leagueName = gameDetails.league.name,
							hasMultipleBowlers = series.value.size > 1,
						),
						scoringMethod = it.scoringMethod.copy(
							score = gameDetails.properties.score,
							scoringMethod = gameDetails.properties.scoringMethod,
							isShowingHighestPossibleScoreButton = featureFlagsClient.isEnabled(
								FeatureFlag.HIGHEST_SCORE_POSSIBLE,
							),
						),
						gameProperties = it.gameProperties.copy(
							locked = gameDetails.properties.locked,
							gameExcludeFromStatistics = gameDetails.properties.excludeFromStatistics,
							seriesExcludeFromStatistics = gameDetails.series.excludeFromStatistics,
							leagueExcludeFromStatistics = gameDetails.league.excludeFromStatistics,
							seriesPreBowl = gameDetails.series.preBowl,
						),
					)
				}

				gamesEditorState.updateGamesEditor(gameToLoad) {
					it.copy(
						manualScore = when (gameDetails.properties.scoringMethod) {
							GameScoringMethod.MANUAL -> gameDetails.properties.score
							GameScoringMethod.BY_FRAME -> null
						},
						frameEditor = it.frameEditor.copy(
							isEnabled = gameDetails.properties.locked != GameLockState.LOCKED,
						),
					)
				}

				lastLoadedGameAt.update { lastLoadedGameAt ->
					if (lastLoadedGameAt?.gameId != gameDetails.properties.id) {
						GameLoadDate(
							gameId = gameDetails.properties.id,
							durationMillisWhenLoaded = gameDetails.properties.durationMillis,
							loadedAt = System.currentTimeMillis(),
						)
					} else {
						lastLoadedGameAt
					}
				}
			}
		}

		seriesDetailsJob?.cancel()
		seriesDetailsJob = viewModelScope.launch(ioDispatcher) {
			val seriesId = currentSeriesId()
			gamesRepository.getGameIds(seriesId).collect { ids ->
				gameDetailsState.updateGameDetails(gameToLoad) {
					it.copy(seriesGameIds = ids)
				}
			}
		}

		bowlerDetailsJob?.cancel()
		bowlerDetailsJob = viewModelScope.launch(ioDispatcher) {
			combine(currentBowlerId.filterNotNull(), bowlers) { currentBowlerId, bowlers ->
				Pair(currentBowlerId, bowlers)
			}.collect { currentBowlers ->
				val (currentBowlerId, bowlers) = currentBowlers
				gameDetailsState.updateGameDetails(gameToLoad) {
					it.copy(
						currentBowlerIndex = bowlers.indexOfFirst { bowler -> bowler.id == currentBowlerId },
						bowlers = bowlers,
					)
				}
			}
		}

		ballsJob?.cancel()
		ballsJob = viewModelScope.launch(ioDispatcher) {
			gearRepository.getRecentlyUsedGear(GearKind.BOWLING_BALL, limit = 4).collect { gear ->
				gamesEditorState.updateGamesEditor(gameToLoad) {
					it.copy(
						rollEditor = it.rollEditor.copy(
							recentBalls = gear
								.sortedBy { item -> item.name }
								.map { item ->
									FrameEdit.Gear(
										id = item.gearId,
										name = item.name,
										kind = item.kind,
										avatar = item.avatar,
									)
								},
						),
					)
				}
			}
		}

		gearJob?.cancel()
		gearJob = viewModelScope.launch(ioDispatcher) {
			gearRepository.getGameGear(gameToLoad).collect { gear ->
				gameDetailsState.updateGameDetails(gameToLoad) {
					it.copy(
						gear = it.gear.copy(
							selectedGear = gear,
						),
					)
				}
			}
		}

		matchPlayJob?.cancel()
		matchPlayJob = viewModelScope.launch(ioDispatcher) {
			matchPlaysRepository.getMatchPlay(gameToLoad).collect { matchPlay ->
				gameDetailsState.updateGameDetails(gameToLoad) {
					it.copy(
						matchPlay = it.matchPlay.copy(
							opponentName = matchPlay?.opponent?.name,
							opponentScore = matchPlay?.opponentScore,
							result = matchPlay?.result,
						),
					)
				}
			}
		}

		alleyJob?.cancel()
		alleyJob = viewModelScope.launch(ioDispatcher) {
			alleysRepository.getGameAlleyDetails(gameToLoad).collect { alley ->
				gameDetailsState.updateGameDetails(gameToLoad) {
					it.copy(
						alley = it.alley.copy(
							selectedAlley = alley,
						),
					)
				}
			}
		}

		lanesJob?.cancel()
		lanesJob = viewModelScope.launch(ioDispatcher) {
			lanesRepository.getGameLanes(gameToLoad).collect { lanes ->
				gameDetailsState.updateGameDetails(gameToLoad) {
					it.copy(
						alley = it.alley.copy(
							selectedLanes = lanes,
						),
					)
				}
			}
		}

		var isInitialFrameLoad = true
		framesJob?.cancel()
		framesJob = viewModelScope.launch(ioDispatcher) {
			framesRepository.getFrames(gameToLoad).collect { frames ->
				gamesEditorState.updateGamesEditor(gameToLoad) {
					it.copy(
						frames = frames,
					).updateFrameEditor()
				}

				if (isInitialFrameLoad) {
					isInitialFrameLoad = false
					val newFrameIndex = frames.nextIndexToRecord()
					val newRollIndex = frames[newFrameIndex].firstUntouchedRoll ?: 0
					setCurrentSelection(frameIndex = newFrameIndex, rollIndex = newRollIndex)
				}
			}
		}
	}

	private fun dismiss() {
		viewModelScope.launch(ioDispatcher) {
			userDataRepository.dismissLatestGameInEditor()
			sendEvent(GamesEditorScreenEvent.Dismissed)
		}
	}

	private fun setLatestGameId(gameId: GameID) {
		viewModelScope.launch(ioDispatcher) {
			userDataRepository.setLatestGameInEditor(gameId)
			userDataRepository.setLatestSeriesInEditor(series.value)
			userDataRepository.setLatestTeamSeriesInEditor(teamSeriesId)
		}
	}

	private fun dismissLatestGameInEditor() {
		// TODO: Do we need to use @ApplicationScope here
		scope.launch {
			if (isFinishedRecording()) {
				userDataRepository.dismissLatestGameInEditor()
			}
		}
	}

	private suspend fun isFinishedRecording(): Boolean {
		val gameDetails = gameDetailsState.value
		val isLastGame = gameDetails.seriesGameIds.lastOrNull() == gameDetails.gameId
		val isLastSeries = series.value.lastOrNull() == currentSeriesId()

		if (!isLastGame || !isLastSeries) return false

		val gameScore = gamesEditorState.value.scoreSheet.game?.frames ?: return false
		val lastFrame = gameScore.lastOrNull() ?: return false

		val isGameFinished = lastFrame.rolls.size == Frame.NUMBER_OF_ROLLS && lastFrame.display != null
		return isGameFinished
	}

	private fun dismissDragHint() {
		viewModelScope.launch(ioDispatcher) {
			userDataRepository.didDismissFrameDragHint()
		}
	}

	private fun openShareSheet() {
		sharingSource.value = SharingSource.Game(currentGameId.value)
	}

	private fun dismissShareSheet() {
		sharingSource.value = null
	}

	private fun openGameSettings() {
		isGameDetailsSheetVisible.value = false
		sendEvent(
			GamesEditorScreenEvent.ShowGamesSettings(teamSeriesId, series.value, currentGameId.value),
		)
	}

	private fun openGearPicker() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		isGameDetailsSheetVisible.value = false
		sendEvent(
			GamesEditorScreenEvent.EditGear(
				gameDetailsState.value.gear.selectedGear.map(GearListItem::gearId).toSet(),
			),
		)
	}

	private fun openLanesPicker() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		isGameDetailsSheetVisible.value = false
		val gameDetails = gameDetailsState.value
		sendEvent(
			GamesEditorScreenEvent.EditLanes(
				alleyId = gameDetails.alley.selectedAlley?.id ?: return,
				laneIds = gameDetails.alley.selectedLanes.map(LaneListItem::id).toSet(),
			),
		)
	}

	private fun openAlleyPicker() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		isGameDetailsSheetVisible.value = false
		sendEvent(
			GamesEditorScreenEvent.EditAlley(
				alleyId = gameDetailsState.value.alley.selectedAlley?.id,
			),
		)
	}

	private fun openMatchPlayManager() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		isGameDetailsSheetVisible.value = false
		sendEvent(GamesEditorScreenEvent.EditMatchPlay(currentGameId.value))
	}

	private fun openSeriesStats() {
		isGameDetailsSheetVisible.value = false
		viewModelScope.launch(ioDispatcher) {
			sendEvent(
				GamesEditorScreenEvent.ShowStatistics(
					filter = TrackableFilter(
						source = TrackableFilter.Source.Series(currentSeriesId()),
					),
				),
			)
		}
	}

	private fun openGameStats() {
		isGameDetailsSheetVisible.value = false
		sendEvent(
			GamesEditorScreenEvent.ShowStatistics(
				filter = TrackableFilter(
					source = TrackableFilter.Source.Game(currentGameId.value),
				),
			),
		)
	}

	private fun openAllBowlersScores() {
		isGameDetailsSheetVisible.value = false
		val gameIndex = gameDetailsState.value.currentGameIndex
		sendEvent(GamesEditorScreenEvent.ShowBowlerScores(series.value, gameIndex))
	}

	private fun openBallRolledPicker() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		isGameDetailsSheetVisible.value = false
		sendEvent(
			GamesEditorScreenEvent.EditRolledBall(gamesEditorState.value.rollEditor.selectedBall?.id),
		)
	}

	private fun openScoreSettings() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		isGameDetailsSheetVisible.value = false
		val gameDetails = gameDetailsState.value
		sendEvent(
			GamesEditorScreenEvent.EditScore(
				score = gameDetails.scoringMethod.score,
				scoringMethod = gameDetails.scoringMethod.scoringMethod,
			),
		)
	}

	private fun toggleGameLocked(isLocked: Boolean) {
		val gameLockState = when (isLocked) {
			true -> GameLockState.LOCKED
			false -> GameLockState.UNLOCKED
		}

		val currentGameId = currentGameId.value

		gamesEditorState.updateGamesEditor(currentGameId) {
			it.copy(
				frameEditor = it.frameEditor.copy(isEnabled = !isLocked),
			)
		}

		val gameDetailsState = gameDetailsState.updateGameDetailsAndGet(currentGameId) {
			it.copy(
				gameProperties = it.gameProperties.copy(
					locked = gameLockState,
				),
			)
		}

		viewModelScope.launch(ioDispatcher) {
			gamesRepository.setGameLockState(
				gameDetailsState.gameId,
				gameDetailsState.gameProperties.locked,
			)
		}
	}

	private fun toggleGameExcludedFromStatistics(isExcluded: Boolean) {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		val excludeFromStatistics = when (isExcluded) {
			true -> ExcludeFromStatistics.EXCLUDE
			false -> ExcludeFromStatistics.INCLUDE
		}

		val gameDetailState = gameDetailsState.updateAndGet {
			it.copy(
				gameProperties = it.gameProperties.copy(
					gameExcludeFromStatistics = excludeFromStatistics,
				),
			)
		}

		viewModelScope.launch(ioDispatcher) {
			gamesRepository.setGameExcludedFromStatistics(
				gameDetailState.gameId,
				gameDetailState.gameProperties.gameExcludeFromStatistics,
			)
		}
	}

	private fun dismissGameLockSnackBar() {
		isGameLockSnackBarVisible.value = false
	}

	private fun dismissHighestPossibleScoreSnackBar() {
		highestScorePossibleAlert.value = null
	}

	private fun goToNext(next: NextGameEditableElement) {
		saveCurrentFrameIfEmpty()

		when (next) {
			is NextGameEditableElement.Roll -> setCurrentSelection(rollIndex = next.rollIndex)
			is NextGameEditableElement.Frame -> setCurrentSelection(
				frameIndex = next.frameIndex,
				rollIndex = 0,
			)
			is NextGameEditableElement.Game -> {
				setFrameEditorAnimation(AnimationDirection.RIGHT_TO_LEFT)
				toggleGameLocked(isLocked = true)
				loadGame(next.game)
			}
			is NextGameEditableElement.BowlerGame -> {
				toggleGameLocked(isLocked = true)
				setFrameEditorAnimation(AnimationDirection.RIGHT_TO_LEFT)
				loadBowlerGame(next.bowler, next.gameIndex)
			}
			is NextGameEditableElement.Bowler -> {
				setFrameEditorAnimation(AnimationDirection.RIGHT_TO_LEFT)
				loadBowlerGame(next.bowler)
			}
		}
	}

	private fun updateSelectedFrame(frameIndex: Int) {
		updateSelectedRoll(frameIndex, 0)
	}

	private fun updateSelectedRoll(frameIndex: Int, rollIndex: Int) {
		setCurrentSelection(frameIndex, rollIndex)
	}

	private fun setCurrentSelection(frameIndex: Int? = null, rollIndex: Int? = null) {
		val gameId = currentGameId.value
		val gamesEditor = gamesEditorState.updateGamesEditorAndGet(gameId) {
			it.updateSelection(frameIndex, rollIndex)
				.updateFrameEditor()
		}

		gameDetailsState.updateGameDetails(gameId) {
			it
				// FIXME: Consider if we want to scroll to / highlight the frame in the team score sheet
				// as it's a bit distracting, and hides the bowler's scores
// 				.updateSelection(gamesEditor.scoreSheet.selection)
				.updateHeader(
					frames = gamesEditor.frames,
					selection = gamesEditor.scoreSheet.selection,
				)
		}
	}

	private fun setFrameEditorAnimation(animation: AnimationDirection?) {
		gamesEditorState.updateGamesEditor(currentGameId.value) {
			it.copy(
				frameEditor = it.frameEditor.copy(nextAnimationDirection = animation),
			)
		}
	}

	private fun handleInteractionWithPins() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}
	}

	private fun updateDownedPins(downedPins: Set<Pin>) {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		val gameId = currentGameId.value
		val gamesEditor = gamesEditorState.updateGamesEditorAndGet(gameId) {
			it.copy(
				frameEditor = it.frameEditor.copy(
					downedPins = downedPins,
				),
				frames = it.frames.toMutableList().also { frames ->
					frames.setPinsDowned(
						frameIndex = it.scoreSheet.selection.frameIndex,
						rollIndex = it.scoreSheet.selection.rollIndex,
						pinsDowned = downedPins,
					)
				},
			)
		}

		gameDetailsState.updateGameDetails(gameId) {
			it.updateHeader(
				frames = gamesEditor.frames,
				selection = gamesEditor.scoreSheet.selection,
			)
		}

		saveFrame(gamesEditor.selectedFrame())
	}

	private fun updateSelectedBall(id: GearID?) {
		if (id == null) {
			updateSelectedBall(ball = null)
		} else {
			viewModelScope.launch(ioDispatcher) {
				val gear = gearRepository.getGearDetails(id).first()
				updateSelectedBall(
					FrameEdit.Gear(
						id = gear.id,
						name = gear.name,
						kind = gear.kind,
						avatar = gear.avatar,
					),
				)
			}
		}
	}

	private fun updateSelectedBall(ball: FrameEdit.Gear?) {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		val gameId = currentGameId.value
		val gamesEditorState = gamesEditorState.updateGamesEditorAndGet(gameId) {
			val updatedFrames = it.frames.toMutableList().also { frames ->
				frames.setBallRolled(
					frameIndex = it.scoreSheet.selection.frameIndex,
					rollIndex = it.scoreSheet.selection.rollIndex,
					ballRolled = ball,
				)
			}

			it.copy(
				rollEditor = it.rollEditor.copy(
					selectedBall = updatedFrames[it.scoreSheet.selection.frameIndex]
						.rolls[it.scoreSheet.selection.rollIndex]
						.bowlingBall,
				),
				frames = updatedFrames,
			)
		}

		val updatedBallId = gamesEditorState.rollEditor.selectedBall?.id
		if (updatedBallId != null) {
			viewModelScope.launch(ioDispatcher) {
				recentlyUsedRepository.didRecentlyUseGear(updatedBallId)
			}
		}

		saveFrame(gamesEditorState.selectedFrame())
	}

	private fun toggleDidFoul(didFoul: Boolean) {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		val gameId = currentGameId.value
		val gamesEditorState = gamesEditorState.updateGamesEditorAndGet(gameId) {
			it.copy(
				rollEditor = it.rollEditor.copy(
					didFoulRoll = didFoul,
				),
				frames = it.frames.toMutableList().also { frames ->
					frames.setDidFoul(
						frameIndex = it.scoreSheet.selection.frameIndex,
						rollIndex = it.scoreSheet.selection.rollIndex,
						didFoul = didFoul,
					)
				},
			)
		}

		saveFrame(gamesEditorState.selectedFrame())
	}

	private fun updateScore(score: Int, scoringMethod: GameScoringMethod) {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		val gameId = currentGameId.value

		if (scoringMethod != gameDetailsState.value.scoringMethod.scoringMethod) {
			when (scoringMethod) {
				GameScoringMethod.MANUAL -> analyticsClient.trackEvent(GameManualScoreSet(gameId = gameId))
				GameScoringMethod.BY_FRAME -> Unit
			}
		}

		gameDetailsState.updateGameDetails(gameId) {
			it.copy(
				scoringMethod = it.scoringMethod.copy(
					score = score,
					scoringMethod = scoringMethod,
				),
			)
		}

		gamesEditorState.updateGamesEditor(gameId) {
			it.copy(
				manualScore = when (scoringMethod) {
					GameScoringMethod.MANUAL -> score
					GameScoringMethod.BY_FRAME -> null
				},
			)
		}

		viewModelScope.launch(ioDispatcher) {
			gamesRepository.setGameScoringMethod(gameId, scoringMethod, score)
		}
	}

	private fun updateAlley(alleyId: AlleyID?) {
		if (isGameLocked) return

		viewModelScope.launch(ioDispatcher) {
			seriesRepository.setSeriesAlley(currentSeriesId(), alleyId)
		}
	}

	private fun updateLanes(laneIds: Set<LaneID>) {
		if (isGameLocked) return

		val currentLaneIds = gameDetailsState.value.alley.selectedLanes.map(LaneListItem::id).toSet()
		if (currentLaneIds.isEmpty() && laneIds.isNotEmpty()) {
			setCopyLanesDialog(isVisible = true)
		}

		viewModelScope.launch(ioDispatcher) {
			gamesRepository.setGameLanes(currentGameId.value, laneIds)
		}
	}

	private fun setCopyLanesDialog(isVisible: Boolean) {
		gameDetailsState.updateGameDetails(currentGameId.value) {
			it.copy(copyLanesDialog = if (isVisible) CopyLanesDialogUiState else null)
		}

		isGameDetailsSheetVisible.value = false
		viewModelScope.launch(ioDispatcher) {
			isGameDetailsSheetVisible.value = true
		}
	}

	private fun copyLanesToAllGames() {
		val laneIds = gameDetailsState.value.alley.selectedLanes.map(LaneListItem::id).toSet()
		setCopyLanesDialog(isVisible = false)
		viewModelScope.launch(ioDispatcher) {
			gamesRepository.setAllGameLanes(currentSeriesId(), laneIds)
		}
	}

	private fun updateGear(gearIds: Set<GearID>) {
		if (isGameLocked) return

		val gameId = currentGameId.value
		viewModelScope.launch(ioDispatcher) {
			gearRepository.setGameGear(gameId, gearIds)
		}
	}

	private fun setHeaderPeekHeight(height: Float) {
		headerPeekHeight.value = height
	}

	private fun saveCurrentFrameIfEmpty() {
		if (isGameLocked) return

		val gamesEditor = gamesEditorState.updateGamesEditorAndGet(currentGameId.value) {
			it.copy(
				frames = it.frames.toMutableList().also { frames ->
					frames.ensureRollExists(
						frameIndex = it.scoreSheet.selection.frameIndex,
						rollIndex = it.scoreSheet.selection.rollIndex,
					)
				},
			)
		}

		saveFrame(gamesEditor.selectedFrame())
	}

	private fun saveFrame(frame: FrameEdit) {
		if (isGameLocked) return

		analyticsClient.trackEvent(GameUpdated(gameId = currentGameId.value))

		viewModelScope.launch(ioDispatcher) {
			framesRepository.updateFrame(frame)
		}
	}

	private val isGameLocked: Boolean
		get() = gameDetailsState.value.gameProperties.locked == GameLockState.LOCKED

	private fun notifyGameLocked() {
		isGameLockSnackBarVisible.value = true
	}

	private suspend fun currentSeriesId(): SeriesID = currentBowlerId
		.filterNotNull()
		.map { getBowlerSeriesId(it) }
		.first()

	private suspend fun getBowlerSeriesId(bowlerId: BowlerID): SeriesID = bowlers.map { bowlers ->
		val bowlerIndex = bowlers.indexOfFirst { it.id == bowlerId }
		if (bowlerIndex == -1) return@map null
		series.value[bowlerIndex]
	}
		.filterNotNull()
		.first()
}
