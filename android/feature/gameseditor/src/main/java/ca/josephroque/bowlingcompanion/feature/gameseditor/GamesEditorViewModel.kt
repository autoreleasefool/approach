package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.toggle
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.INITIAL_GAME_ID
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.SERIES_ID
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GamePropertiesCardUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GearCardUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.HeaderUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.MatchPlayCardUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.NextGameEditableElement
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.ScoringMethodCardUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GamesEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val gamesRepository: GamesRepository,
): ViewModel() {
	private val seriesId = UUID.fromString(savedStateHandle[SERIES_ID])
	private val initialGameId = UUID.fromString(savedStateHandle[INITIAL_GAME_ID])

	private var _gamesEditorState = MutableStateFlow(GamesEditorUiState())
	val gamesEditorState = _gamesEditorState.asStateFlow()

	private val _frameEditorState = MutableStateFlow(FrameEditorUiState())
	val frameEditorState = _frameEditorState.asStateFlow()

	private val _rollEditorState = MutableStateFlow(RollEditorUiState())
	val rollEditorState = _rollEditorState.asStateFlow()

	private val _scoreSheetState = MutableStateFlow(ScoreSheetUiState())
	val scoreSheetState = _scoreSheetState.asStateFlow()

	private var _gameDetailsJob: Job? = null
	private val _gameDetailsState = MutableStateFlow(GameDetailsUiState())
	val gameDetailsState = _gameDetailsState.asStateFlow()

	fun loadGame(gameId: UUID? = null) {
		val gameToLoad = gameId ?: initialGameId

		_gamesEditorState.value = _gamesEditorState.value.copy(didLoadInitialGame = true)
		_gameDetailsJob?.cancel()

		viewModelScope.launch {
			gamesRepository.getGameDetails(gameToLoad)
				.collect {
					_gameDetailsState.value = GameDetailsUiState(
						currentGameIndex = it.properties.index,
						header = HeaderUiState(
							bowlerName = it.bowler.name,
							leagueName = it.league.name,
							nextElement = null, // TODO: update next header element
						),
						gear = GearCardUiState(
							selectedGear = emptyList(), // TODO: load selected gear
						),
						matchPlay = MatchPlayCardUiState(
							opponentName = null, // TODO: load opponent name
							opponentScore = null, // TODO: load opponent score
							result = null, // TODO: load match play result
						),
						scoringMethod = ScoringMethodCardUiState(
							score = it.properties.score,
							scoringMethod = it.properties.scoringMethod,
						),
						gameProperties = GamePropertiesCardUiState(
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

	fun openGameSettings() {
		/* TODO: openGameSettings */
	}

	fun openGearPicker() {
		/* TODO: openGearPicker */
	}

	fun openMatchPlayManager() {
		/* TODO: openMatchPlayManager */
	}

	fun goToNext(next: NextGameEditableElement) {
		/* TODO: goToNext */
	}

	fun openSeriesStats() {
		/* TODO: openSeriesStats */
	}

	fun openGameStats() {
		/* TODO: openGameStats */
	}

	fun openScoreSettings() {
		/* TODO: openScoreSettings */
	}

	fun toggleGameLocked(isLocked: Boolean?) {
		val state = _gameDetailsState.value
		_gameDetailsState.value = state.copy(
			gameProperties = state.gameProperties.copy(
				locked = when (isLocked) {
					true -> GameLockState.LOCKED
					false -> GameLockState.UNLOCKED
					null -> when (state.gameProperties.locked) {
						GameLockState.LOCKED -> GameLockState.UNLOCKED
						GameLockState.UNLOCKED -> GameLockState.LOCKED
					}
				}
			)
		)
		// TODO: save game
	}

	fun toggleGameExcludedFromStatistics(isExcluded: Boolean?) {
		val state = _gameDetailsState.value
		_gameDetailsState.value = state.copy(
			gameProperties = state.gameProperties.copy(
				gameExcludeFromStatistics = when (isExcluded) {
					true -> ExcludeFromStatistics.EXCLUDE
					false -> ExcludeFromStatistics.INCLUDE
					null -> state.gameProperties.gameExcludeFromStatistics.toggle()
				}
			)
		)
		// TODO: save game
	}

	fun updateFrameSelection(selection: ScoreSheetUiState.Selection) {
		_scoreSheetState.value = _scoreSheetState.value.copy(selection = selection)
		// TODO: update frame to correct roll
	}

	fun updateDownedPins(downedPins: Set<Pin>) {
		_frameEditorState.value = _frameEditorState.value.copy(downedPins = downedPins)
		// TODO: save frame
	}

	fun updateSelectedBall(ballId: UUID) {
		_rollEditorState.value = _rollEditorState.value.copy(selectedBall = ballId)
		// TODO: save selected ball
	}

	fun toggleFoul(isFoul: Boolean) {
		_rollEditorState.value = _rollEditorState.value.copy(didFoulRoll = isFoul)
		// TODO: save frame
	}
}

data class GamesEditorUiState(
	val didLoadInitialGame: Boolean = false,
)