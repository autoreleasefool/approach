package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.INITIAL_GAME_ID
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.SERIES_ID
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.NextGameEditableElement
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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

	private val _frameEditorState = MutableStateFlow<FrameEditorUiState>(FrameEditorUiState.Loading)
	val frameEditorState = _frameEditorState.asStateFlow()

	private val _rollEditorState = MutableStateFlow<RollEditorUiState>(RollEditorUiState.Loading)
	val rollEditorState = _rollEditorState.asStateFlow()

	private var _gameDetailsJob: Job? = null
	private val _gameDetailsState = MutableStateFlow<GameDetailsUiState>(GameDetailsUiState.Loading)
	val gameDetailsState = _gameDetailsState.asStateFlow()

	fun loadGame(gameId: UUID? = null) {
		val gameToLoad = gameId ?: initialGameId

		_gamesEditorState.value = _gamesEditorState.value.copy(didLoadInitialGame = true)
		_gameDetailsJob?.cancel()

		viewModelScope.launch {
			gamesRepository.getGameDetails(gameToLoad)
				.collect {
					_gameDetailsState.value = GameDetailsUiState.Edit(
						bowlerName = it.bowler.name,
						leagueName = it.league.name,
						currentGameIndex = it.properties.index,
						selectedGear = listOf(), // TODO: load selected gear
						opponentName = null, // TODO: load opponent name
						opponentScore = null, // TODO: load opponent score
						matchPlayResult = null, // TODO: load match play result
						nextElement = null, // TODO: update next header element
					)

					// TODO: update frame state
					_frameEditorState.value = FrameEditorUiState.Edit(
						lockedPins = setOf(),
						downedPins = setOf(),
					)

					// TODO: update roll state
					_rollEditorState.value = RollEditorUiState.Edit(
						recentBalls = listOf(),
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
}

data class GamesEditorUiState(
	val didLoadInitialGame: Boolean = false
)