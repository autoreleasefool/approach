package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.MatchPlaysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.ScoresRepository
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiAction
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.INITIAL_GAME_ID
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.SERIES_ID
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.NextGameEditableElement
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GamesEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val gamesRepository: GamesRepository,
	private val gearRepository: GearRepository,
	private val matchPlaysRepository: MatchPlaysRepository,
	private val scoresRepository: ScoresRepository,
): ApproachViewModel<GamesEditorScreenEvent>() {
	private val seriesId = UUID.fromString(savedStateHandle[SERIES_ID])
	private val initialGameId = UUID.fromString(savedStateHandle[INITIAL_GAME_ID])
	private var _currentGameId: UUID = initialGameId

	private val _headerPeekHeight = MutableStateFlow(0f)

	private var _framesJob: Job? = null
	private val _frameEditorState = MutableStateFlow(FrameEditorUiState())
	private val _rollEditorState = MutableStateFlow(RollEditorUiState())

	private var _scoresJob: Job? = null
	private val _scoreSheetState = MutableStateFlow(ScoreSheetUiState())

	private val _scoreEditor: MutableStateFlow<ScoreEditorUiState?> = MutableStateFlow(null)

	private val _gamesEditorState = combine(
		_frameEditorState,
		_rollEditorState,
		_scoreSheetState,
		_scoreEditor,
	) { frameEditor, rollEditor, scoreSheet, scoreEditor ->
		GamesEditorUiState(
			frameEditor = frameEditor,
			rollEditor = rollEditor,
			scoreSheet = scoreSheet,
			scoreEditor = scoreEditor,
		)
	}

	private var _gameDetailsJob: Job? = null
	private var _gearJob: Job? = null
	private var _matchPlayJob: Job? = null
	private val _gameDetailsState = MutableStateFlow(GameDetailsUiState())

	val uiState: StateFlow<GamesEditorScreenUiState> = combine(
		_gamesEditorState,
		_gameDetailsState,
		_headerPeekHeight,
	) { gamesEditor, gameDetails, headerPeekHeight ->
		GamesEditorScreenUiState.Loaded(
			gamesEditor = gamesEditor,
			gameDetails = gameDetails,
			headerPeekHeight = headerPeekHeight,
		)
	}.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		GamesEditorScreenUiState.Loading,
	)

	fun handleAction(action: GamesEditorScreenUiAction) {
		when (action) {
			GamesEditorScreenUiAction.LoadInitialGame -> loadGame()
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
			is FrameEditorUiAction.DownedPinsChanged -> updateDownedPins(action.downedPins)
		}
	}

	private fun handleRollEditorAction(action: RollEditorUiAction) {
		when (action) {
			RollEditorUiAction.PickBallClicked -> openBallRolledPicker()
			is RollEditorUiAction.BallClicked -> updateSelectedBall(action.ball)
			is RollEditorUiAction.FoulToggled -> toggleFoul(action.foul)
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
			is ScoreEditorUiAction.ScoreChanged -> _scoreEditor.value = _scoreEditor.value?.copy(score = action.score.toIntOrNull() ?: 0)
			is ScoreEditorUiAction.ScoringMethodChanged -> _scoreEditor.value = _scoreEditor.value?.copy(scoringMethod = action.scoringMethod)
		}
	}

	private fun loadGame(gameId: UUID? = null) {
		_currentGameId = gameId ?: initialGameId
		val gameToLoad = _currentGameId

		_gameDetailsJob?.cancel()
		_gameDetailsJob = viewModelScope.launch {
			gamesRepository.getGameDetails(gameToLoad).collect { gameDetails ->
				_gameDetailsState.update {
					it.copy(
						currentGameId = gameDetails.properties.id,
						currentGameIndex = gameDetails.properties.index,
						header = it.header.copy(
							bowlerName = gameDetails.bowler.name,
							leagueName = gameDetails.league.name,
							nextElement = null, // TODO: Update next header element
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

		_gearJob?.cancel()
		_gearJob = viewModelScope.launch {
			gearRepository.getGameGear(gameToLoad).collect { gear ->
				_gameDetailsState.update {
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
				_gameDetailsState.update {
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
				_scoreSheetState.update {
					it.copy(
						game = score
					)
				}
			}
		}

		_framesJob?.cancel()
		_framesJob = viewModelScope.launch {
			// TODO: update frame state
			// TODO: update roll state
		}
	}

	private fun updateGear(gearIds: Set<UUID>) {
		viewModelScope.launch {
			gearRepository.setGameGear(_currentGameId, gearIds)
		}
	}

	private fun openGameSettings() {
		/* TODO: openGameSettings */
	}

	private fun openGearPicker() {
		sendEvent(GamesEditorScreenEvent.EditGear(
			_gameDetailsState.value.gear.selectedGear.map(GearListItem::id).toSet()
		))
	}

	private fun openMatchPlayManager() {
		sendEvent(GamesEditorScreenEvent.EditMatchPlay(_currentGameId))
	}

	private fun goToNext(next: NextGameEditableElement) {
		/* TODO: goToNext */
	}

	private fun openSeriesStats() {
		/* TODO: openSeriesStats */
	}

	private fun openGameStats() {
		/* TODO: openGameStats */
	}

	private fun openScoreSettings() {
		val state = _gameDetailsState.value
		_scoreEditor.value = ScoreEditorUiState(
			score = state.scoringMethod.score,
			scoringMethod = state.scoringMethod.scoringMethod,
		)
	}

	private fun dismissScoreEditor(didSave: Boolean) {
		val currentGameId = _currentGameId
		val scoreEditor = _scoreEditor.value ?: return
		if (didSave) {
			viewModelScope.launch {
				when (scoreEditor.scoringMethod) {
					GameScoringMethod.MANUAL ->
						gamesRepository.setGameScoringMethod(currentGameId, scoreEditor.scoringMethod, scoreEditor.score)
					GameScoringMethod.BY_FRAME -> {
						// TODO: Calculate score of game from frames
						gamesRepository.setGameScoringMethod(currentGameId, scoreEditor.scoringMethod, 0)
					}
				}
			}
		}
		_scoreEditor.value = null
	}

	private fun openBallRolledPicker() {
		/* TODO: openBallRolledPicker */
	}

	private fun toggleGameLocked(isLocked: Boolean) {
		val state = _gameDetailsState.value
		val gameLockState = when (isLocked) {
			true -> GameLockState.LOCKED
			false -> GameLockState.UNLOCKED
		}

		_gameDetailsState.value = state.copy(
			gameProperties = state.gameProperties.copy(
				locked = gameLockState
			)
		)

		viewModelScope.launch {
			gamesRepository.setGameLockState(_currentGameId, gameLockState)
		}
	}

	private fun toggleGameExcludedFromStatistics(isExcluded: Boolean) {
		val excludeFromStatistics = when (isExcluded) {
			true -> ExcludeFromStatistics.EXCLUDE
			false -> ExcludeFromStatistics.INCLUDE
		}

		_gameDetailsState.update {
			it.copy(
				gameProperties = it.gameProperties.copy(
					gameExcludeFromStatistics = excludeFromStatistics
				),
			)
		}

		viewModelScope.launch {
			gamesRepository.setGameExcludedFromStatistics(_currentGameId, excludeFromStatistics)
		}
	}

	private fun updateSelectedFrame(frameIndex: Int) {
		updateSelectedRoll(frameIndex, 0)
	}

	private fun updateSelectedRoll(frameIndex: Int, rollIndex: Int) {
		// TODO: update roll selection
	}

	private fun updateDownedPins(downedPins: Set<Pin>) {
		_frameEditorState.value = _frameEditorState.value.copy(downedPins = downedPins)
		// TODO: save frame
	}

	private fun updateSelectedBall(ballId: UUID) {
		_rollEditorState.value = _rollEditorState.value.copy(selectedBall = ballId)
		// TODO: save selected ball
	}

	private fun toggleFoul(isFoul: Boolean) {
		_rollEditorState.value = _rollEditorState.value.copy(didFoulRoll = isFoul)
		// TODO: save frame
	}

	private fun setHeaderPeekHeight(height: Float) {
		_headerPeekHeight.value = height
	}
}