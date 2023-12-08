package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameLockState
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GamesEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val gamesRepository: GamesRepository,
): ApproachViewModel<GamesEditorScreenEvent>() {
	private val seriesId = UUID.fromString(savedStateHandle[SERIES_ID])
	private val initialGameId = UUID.fromString(savedStateHandle[INITIAL_GAME_ID])

	private val _headerPeekHeight = MutableStateFlow(0f)

	private val _frameEditorState = MutableStateFlow(FrameEditorUiState())

	private val _rollEditorState = MutableStateFlow(RollEditorUiState())

	private val _scoreSheetState = MutableStateFlow(ScoreSheetUiState())

	private val _gamesEditorState = combine(
		_frameEditorState,
		_rollEditorState,
		_scoreSheetState,
	) { frameEditor, rollEditor, scoreSheet ->
		GamesEditorUiState(
			frameEditor = frameEditor,
			rollEditor = rollEditor,
			scoreSheet = scoreSheet,
		)
	}

	private var _gameDetailsJob: Job? = null
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

	private fun loadGame(gameId: UUID? = null) {
		val gameToLoad = gameId ?: initialGameId

		_gameDetailsJob?.cancel()

		viewModelScope.launch {
			gamesRepository.getGameDetails(gameToLoad)
				.collect {
					_gameDetailsState.value = GameDetailsUiState(
						currentGameId = it.properties.id,
						currentGameIndex = it.properties.index,
						header = GameDetailsUiState.HeaderUiState(
							bowlerName = it.bowler.name,
							leagueName = it.league.name,
							nextElement = null, // TODO: update next header element
						),
						gear = GameDetailsUiState.GearCardUiState(
							selectedGear = emptyList(), // TODO: load selected gear
						),
						matchPlay = GameDetailsUiState.MatchPlayCardUiState(
							opponentName = it.matchPlay?.opponent?.name,
							opponentScore = it.matchPlay?.opponentScore,
							result = it.matchPlay?.result,
						),
						scoringMethod = GameDetailsUiState.ScoringMethodCardUiState(
							score = it.properties.score,
							scoringMethod = it.properties.scoringMethod,
						),
						gameProperties = GameDetailsUiState.GamePropertiesCardUiState(
							locked = it.properties.locked,
							gameExcludeFromStatistics = it.properties.excludeFromStatistics,
							seriesExcludeFromStatistics = it.series.excludeFromStatistics,
							leagueExcludeFromStatistics = it.league.excludeFromStatistics,
							seriesPreBowl = it.series.preBowl,
						),
					)

					// TODO: update frame state
					_frameEditorState.value = FrameEditorUiState(
						lockedPins = emptySet(),
						downedPins = emptySet(),
					)

					// TODO: update roll state
					_rollEditorState.value = RollEditorUiState(
						recentBalls = emptyList(),
						didFoulRoll = false,
						selectedBall = null,
					)
				}
		}
	}

	private fun openGameSettings() {
		/* TODO: openGameSettings */
	}

	private fun openGearPicker() {
		/* TODO: openGearPicker */
	}

	private fun openMatchPlayManager() {
		val gameId = _gameDetailsState.value.currentGameId ?: return
		sendEvent(GamesEditorScreenEvent.EditMatchPlay(gameId))
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
		/* TODO: openScoreSettings */
	}

	private fun openBallRolledPicker() {
		/* TODO: openBallRolledPicker */
	}

	private fun toggleGameLocked(isLocked: Boolean?) {
		val state = _gameDetailsState.value
		_gameDetailsState.value = state.copy(
			gameProperties = state.gameProperties.copy(
				locked = when (isLocked) {
					true -> GameLockState.LOCKED
					false -> GameLockState.UNLOCKED
					null -> state.gameProperties.locked.next
				}
			)
		)
		// TODO: save game
	}

	private fun toggleGameExcludedFromStatistics(isExcluded: Boolean?) {
		val state = _gameDetailsState.value
		_gameDetailsState.value = state.copy(
			gameProperties = state.gameProperties.copy(
				gameExcludeFromStatistics = when (isExcluded) {
					true -> ExcludeFromStatistics.EXCLUDE
					false -> ExcludeFromStatistics.INCLUDE
					null -> state.gameProperties.gameExcludeFromStatistics.next
				}
			)
		)
		// TODO: save game
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