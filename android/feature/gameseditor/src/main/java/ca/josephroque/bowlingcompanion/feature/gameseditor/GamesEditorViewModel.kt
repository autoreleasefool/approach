package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.FramesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.MatchPlaysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.ScoresRepository
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.nextIndexToRecord
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.INITIAL_GAME_ID
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.SERIES_ID
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.NextGameEditableElement
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.getAndUpdateGamesEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.selectedFrame
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.setBallRolled
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.setDidFoul
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.setPinsDowned
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateFrameEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateGameDetails
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateGameDetailsAndGet
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateGamesEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateGamesEditorAndGet
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateHeader
import ca.josephroque.bowlingcompanion.feature.gameseditor.utils.updateSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GamesEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val framesRepository: FramesRepository,
	private val gamesRepository: GamesRepository,
	private val gearRepository: GearRepository,
	private val matchPlaysRepository: MatchPlaysRepository,
	private val scoresRepository: ScoresRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
): ApproachViewModel<GamesEditorScreenEvent>() {
	private val seriesId = UUID.fromString(savedStateHandle[SERIES_ID])
	private val initialGameId = UUID.fromString(savedStateHandle[INITIAL_GAME_ID])

	private val _currentGameId = MutableStateFlow(initialGameId)
	private val _headerPeekHeight = MutableStateFlow(0f)
	private val _isGameLockSnackBarVisible = MutableStateFlow(false)

	private var _ballsJob: Job? = null
	private var _framesJob: Job? = null
	private var _scoresJob: Job? = null
	private val _gamesEditorState = MutableStateFlow(GamesEditorUiState(gameId = initialGameId))

	private var _gearJob: Job? = null
	private var _matchPlayJob: Job? = null
	private var _gameDetailsJob: Job? = null
	private val _gameDetailsState = MutableStateFlow(GameDetailsUiState(gameId = initialGameId))

	val uiState: StateFlow<GamesEditorScreenUiState> = combine(
		_gamesEditorState,
		_gameDetailsState,
		_headerPeekHeight,
		_isGameLockSnackBarVisible,
	) { gamesEditor, gameDetails, headerPeekHeight, isGameLockSnackBarVisible ->
		GamesEditorScreenUiState.Loaded(
			gamesEditor = gamesEditor,
			gameDetails = gameDetails,
			headerPeekHeight = headerPeekHeight,
			isGameLockSnackBarVisible = isGameLockSnackBarVisible
		)
	}.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		GamesEditorScreenUiState.Loading,
	)

	fun handleAction(action: GamesEditorScreenUiAction) {
		when (action) {
			GamesEditorScreenUiAction.LoadInitialGame -> loadGame()
			GamesEditorScreenUiAction.GameLockSnackBarDismissed -> dismissGameLockSnackBar()
			is GamesEditorScreenUiAction.GamesEditor -> handleGamesEditorAction(action.action)
			is GamesEditorScreenUiAction.GameDetails -> handleGameDetailsAction(action.action)
			is GamesEditorScreenUiAction.GearUpdated -> updateGear(action.gearIds)
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
			is GameDetailsUiAction.LockToggled -> toggleGameLocked(action.locked)
			is GameDetailsUiAction.ExcludeFromStatisticsToggled -> toggleGameExcludedFromStatistics(action.excludeFromStatistics)
			is GameDetailsUiAction.NextGameElementClicked -> goToNext(action.nextGameElement)
			is GameDetailsUiAction.HeaderHeightMeasured -> setHeaderPeekHeight(action.height)
		}
	}

	private fun handleGamesEditorAction(action: GamesEditorUiAction) {
		when (action) {
			GamesEditorUiAction.BackClicked -> sendEvent(GamesEditorScreenEvent.Dismissed)
			GamesEditorUiAction.SettingsClicked -> openGameSettings()
			is GamesEditorUiAction.FrameEditor -> handleFrameEditorAction(action.action)
			is GamesEditorUiAction.RollEditor -> handleRollEditorAction(action.action)
			is GamesEditorUiAction.ScoreSheet -> handleScoreSheetAction(action.action)
			is GamesEditorUiAction.ScoreEditor -> handleScoreEditorAction(action.action)
		}
	}

	private fun handleFrameEditorAction(action: FrameEditorUiAction) {
		when (action) {
			FrameEditorUiAction.FrameEditorInteractionStarted -> handleInteractionWithPins()
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

	private fun handleScoreEditorAction(action: ScoreEditorUiAction) {
		when (action) {
			ScoreEditorUiAction.CancelClicked -> dismissScoreEditor(didSave = false)
			ScoreEditorUiAction.SaveClicked -> dismissScoreEditor(didSave = true)
			is ScoreEditorUiAction.ScoreChanged -> updateScoreEditorScore(score = action.score)
			is ScoreEditorUiAction.ScoringMethodChanged -> updateScoreEditorScoringMethod(score = action.scoringMethod)
		}
	}

	private fun loadGame(gameId: UUID? = null) {
		val gameToLoad = _currentGameId.updateAndGet { gameId ?: initialGameId }

		_gameDetailsJob?.cancel()
		_gameDetailsJob = viewModelScope.launch {
			gamesRepository.getGameDetails(gameToLoad).collect { gameDetails ->
				_gameDetailsState.updateGameDetails(gameToLoad) {
					it.copy(
						gameId = gameDetails.properties.id,
						currentGameIndex = gameDetails.properties.index,
						header = it.header.copy(
							bowlerName = gameDetails.bowler.name,
							leagueName = gameDetails.league.name,
						),
						scoringMethod = it.scoringMethod.copy(
							score = gameDetails.properties.score,
							scoringMethod = gameDetails.properties.scoringMethod,
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
			}
		}

		_ballsJob?.cancel()
		_ballsJob = viewModelScope.launch {
			gearRepository.getRecentlyUsedGear(GearKind.BOWLING_BALL, limit = 4).collect { gear ->
				_gamesEditorState.updateGamesEditor(gameToLoad) {
					it.copy(
						rollEditor = it.rollEditor.copy(
							recentBalls = gear
								.sortedBy { item -> item.name }
								.map { item -> FrameEdit.Gear(id = item.id, name = item.name, kind = item.kind, avatar = item.avatar) },
						),
					)
				}
			}
		}

		_gearJob?.cancel()
		_gearJob = viewModelScope.launch {
			gearRepository.getGameGear(gameToLoad).collect { gear ->
				_gameDetailsState.updateGameDetails(gameToLoad) {
					it.copy(
						gear = it.gear.copy(
							selectedGear = gear,
						)
					)
				}
			}
		}

		_matchPlayJob?.cancel()
		_matchPlayJob = viewModelScope.launch {
			matchPlaysRepository.getMatchPlay(gameToLoad).collect { matchPlay ->
				_gameDetailsState.updateGameDetails(gameToLoad) {
					it.copy(
						matchPlay = it.matchPlay.copy(
							opponentName = matchPlay?.opponent?.name,
							opponentScore = matchPlay?.opponentScore,
							result = matchPlay?.result,
						)
					)
				}
			}
		}

		_scoresJob?.cancel()
		_scoresJob = viewModelScope.launch {
			scoresRepository.getScore(gameToLoad).collect { score ->
				_gamesEditorState.updateGamesEditor(gameToLoad) {
					it.copy(
						scoreSheet = it.scoreSheet.copy(
							game = score,
						),
					)
				}

				val gameDetails = _gameDetailsState.updateGameDetailsAndGet(gameToLoad) {
					when (it.scoringMethod.scoringMethod) {
						GameScoringMethod.MANUAL -> it
						GameScoringMethod.BY_FRAME -> it.copy(
							scoringMethod = it.scoringMethod.copy(score = score.score ?: 0),
						)
					}
				}

				when (gameDetails.scoringMethod.scoringMethod) {
					GameScoringMethod.MANUAL -> Unit
					GameScoringMethod.BY_FRAME -> viewModelScope.launch {
						gamesRepository.setGameScore(
							gameDetails.gameId,
							gameDetails.scoringMethod.score,
						)
					}
				}
			}
		}

		var isInitialFrameLoad = true
		_framesJob?.cancel()
		_framesJob = viewModelScope.launch {
			framesRepository.getFrames(gameToLoad).collect { frames ->
				_gamesEditorState.updateGamesEditor(gameToLoad) {
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

	private fun openGameSettings() {
		/* TODO: openGameSettings */
	}

	private fun openGearPicker() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		sendEvent(GamesEditorScreenEvent.EditGear(
			_gameDetailsState.value.gear.selectedGear.map(GearListItem::id).toSet()
		))
	}

	private fun openLanesPicker() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		val gameDetails = _gameDetailsState.value
		sendEvent(GamesEditorScreenEvent.EditLanes(
			alleyId = gameDetails.alley.selectedAlley?.id ?: return,
			laneIds = gameDetails.alley.selectedLanes.map(LaneListItem::id).toSet()
		))
	}

	private fun openAlleyPicker() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		val gameDetails = _gameDetailsState.value
		sendEvent(GamesEditorScreenEvent.EditAlley(
			alleyId = gameDetails.alley.selectedAlley?.id ?: return
		))
	}

	private fun openMatchPlayManager() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		sendEvent(GamesEditorScreenEvent.EditMatchPlay(_currentGameId.value))
	}

	private fun openSeriesStats() {
		/* TODO: openSeriesStats */
	}

	private fun openGameStats() {
		/* TODO: openGameStats */
	}

	private fun openBallRolledPicker() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		/* TODO: openBallRolledPicker */
	}

	private fun openScoreSettings() {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		val gameDetails = _gameDetailsState.value
		_gamesEditorState.updateGamesEditor(gameDetails.gameId) {
			it.copy(
				scoreEditor = ScoreEditorUiState(
					score = gameDetails.scoringMethod.score,
					scoringMethod = gameDetails.scoringMethod.scoringMethod,
				)
			)
		}
	}

	private fun toggleGameLocked(isLocked: Boolean) {
		val gameLockState = when (isLocked) {
			true -> GameLockState.LOCKED
			false -> GameLockState.UNLOCKED
		}

		val currentGameId = _currentGameId.value

		_gamesEditorState.updateGamesEditor(currentGameId) {
			it.copy(
				frameEditor = it.frameEditor.copy(isEnabled = !isLocked)
			)
		}

		val gameDetailsState = _gameDetailsState.updateGameDetailsAndGet(currentGameId) {
			it.copy(
				gameProperties = it.gameProperties.copy(
					locked = gameLockState,
				),
			)
		}

		viewModelScope.launch {
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

		val gameDetailState = _gameDetailsState.updateAndGet {
			it.copy(
				gameProperties = it.gameProperties.copy(
					gameExcludeFromStatistics = excludeFromStatistics
				),
			)
		}

		viewModelScope.launch {
			gamesRepository.setGameExcludedFromStatistics(
				gameDetailState.gameId,
				gameDetailState.gameProperties.gameExcludeFromStatistics,
			)
		}
	}

	private fun dismissGameLockSnackBar() {
		_isGameLockSnackBarVisible.value = false
	}

	private fun goToNext(next: NextGameEditableElement) {
		when (next) {
			is NextGameEditableElement.Roll -> setCurrentSelection(rollIndex = next.rollIndex)
			is NextGameEditableElement.Frame -> setCurrentSelection(frameIndex = next.frameIndex, rollIndex = 0)
			is NextGameEditableElement.Game -> Unit // TODO: Change game
		}
	}

	private fun updateSelectedFrame(frameIndex: Int) {
		updateSelectedRoll(frameIndex, 0)
	}

	private fun updateSelectedRoll(frameIndex: Int, rollIndex: Int) {
		setCurrentSelection(frameIndex, rollIndex)
	}

	private fun setCurrentSelection(
		frameIndex: Int? = null,
		rollIndex: Int? = null,
	) {
		val gameId = _currentGameId.value
		val gamesEditor = _gamesEditorState.updateGamesEditorAndGet(gameId) {
			it.updateSelection(frameIndex, rollIndex)
				.updateFrameEditor()
		}

		_gameDetailsState.updateGameDetails(gameId) {
			it.updateHeader(
				frames = gamesEditor.frames,
				selection = gamesEditor.scoreSheet.selection,
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

		val gameId = _currentGameId.value
		val gamesEditorState = _gamesEditorState.updateGamesEditorAndGet(gameId) {
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

		saveFrame(gamesEditorState.selectedFrame())
	}

	private fun updateSelectedBall(ball: FrameEdit.Gear) {
		if (isGameLocked) {
			notifyGameLocked()
			return
		}

		val gameId = _currentGameId.value
		val gamesEditorState = _gamesEditorState.updateGamesEditorAndGet(gameId) {
			val updatedFrames = it.frames.toMutableList().also { frames ->
				frames.setBallRolled(
					frameIndex = it.scoreSheet.selection.frameIndex,
					rollIndex = it.scoreSheet.selection.rollIndex,
					ballRolled = ball,
				)
			}

			it.copy(
				rollEditor = it.rollEditor.copy(
					selectedBall = updatedFrames[it.scoreSheet.selection.frameIndex].rolls[it.scoreSheet.selection.rollIndex].bowlingBall
				),
				frames = updatedFrames,
			)
		}

		val updatedBallId = gamesEditorState.rollEditor.selectedBall?.id
		if (updatedBallId != null) {
			viewModelScope.launch {
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

		val gameId = _currentGameId.value
		val gamesEditorState = _gamesEditorState.updateGamesEditorAndGet(gameId) {
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

	private fun updateScoreEditorScore(score: String) {
		_gamesEditorState.update {
			it.copy(
				scoreEditor = it.scoreEditor?.copy(
					score = score.toIntOrNull()?.coerceIn(0, Game.MaxScore) ?: 0,
				),
			)
		}
	}

	private fun updateScoreEditorScoringMethod(score: GameScoringMethod) {
		_gamesEditorState.update {
			it.copy(
				scoreEditor = it.scoreEditor?.copy(
					scoringMethod = score,
				),
			)
		}
	}

	private fun dismissScoreEditor(didSave: Boolean) {
		val gamesEditorState = _gamesEditorState.getAndUpdateGamesEditor(_currentGameId.value) {
			it.copy(scoreEditor = null)
		}

		val scoreEditor = gamesEditorState.scoreEditor ?: return
		if (didSave && !isGameLocked) {
			viewModelScope.launch {
				val score = when (scoreEditor.scoringMethod) {
					GameScoringMethod.MANUAL -> scoreEditor.score
					GameScoringMethod.BY_FRAME -> scoresRepository.getScore(gamesEditorState.gameId).first().score ?: 0
				}

				gamesRepository.setGameScoringMethod(
					gamesEditorState.gameId,
					scoreEditor.scoringMethod,
					score,
				)
			}
		}
	}

	private fun updateGear(gearIds: Set<UUID>) {
		if (isGameLocked) return

		val gameId = _currentGameId.value
		viewModelScope.launch {
			gearRepository.setGameGear(gameId, gearIds)
		}
	}

	private fun setHeaderPeekHeight(height: Float) {
		_headerPeekHeight.value = height
	}

	private fun saveFrame(frame: FrameEdit) {
		if (isGameLocked) return

		viewModelScope.launch {
			framesRepository.updateFrame(frame)
		}
	}

	private val isGameLocked: Boolean
		get() = _gameDetailsState.value.gameProperties.locked == GameLockState.LOCKED

	private fun notifyGameLocked() {
		_isGameLockSnackBarVisible.value = true
	}
}