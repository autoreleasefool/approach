package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.INITIAL_GAME_ID
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.SERIES_ID
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.NextGameEditableElement
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GamesEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
): ViewModel() {
	private val seriesId = UUID.fromString(savedStateHandle[SERIES_ID])
	private val initialGameId = UUID.fromString(savedStateHandle[INITIAL_GAME_ID])

	private val _frameEditorState = MutableStateFlow(FrameEditorUiState.Loading)
	val frameEditorState = _frameEditorState.asStateFlow()

	private val _rollEditorState = MutableStateFlow(RollEditorUiState.Loading)
	val rollEditorState = _rollEditorState.asStateFlow()

	private val _gameDetailsState = MutableStateFlow(GameDetailsUiState.Loading)
	val gameDetailsState = _gameDetailsState.asStateFlow()

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