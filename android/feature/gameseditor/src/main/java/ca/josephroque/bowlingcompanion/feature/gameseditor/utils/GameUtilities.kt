package ca.josephroque.bowlingcompanion.feature.gameseditor.utils

import ca.josephroque.bowlingcompanion.core.model.Frame
import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.Roll
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.isGameFinished
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.NextGameEditableElement
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

data class GameLoadDate(
	val gameId: UUID,
	val durationMillisWhenLoaded: Long,
	val loadedAt: Long,
)

inline fun MutableStateFlow<GameDetailsUiState>.updateGameDetails(
	gameId: UUID,
	function: (GameDetailsUiState) -> GameDetailsUiState,
) {
	this.update { state ->
		if (state.gameId != gameId) {
			state
		} else {
			function(state)
		}
	}
}

inline fun MutableStateFlow<GameDetailsUiState>.updateGameDetailsAndGet(
	gameId: UUID,
	function: (GameDetailsUiState) -> GameDetailsUiState,
) = this.updateAndGet { state ->
	if (state.gameId != gameId) {
		state
	} else {
		function(state)
	}
}

inline fun MutableStateFlow<GamesEditorUiState>.updateGamesEditor(
	gameId: UUID,
	function: (GamesEditorUiState) -> GamesEditorUiState,
) {
	this.update { state ->
		if (state.gameId != gameId) {
			state
		} else {
			function(state)
		}
	}
}

inline fun MutableStateFlow<GamesEditorUiState>.updateGamesEditorAndGet(
	gameId: UUID,
	function: (GamesEditorUiState) -> GamesEditorUiState,
): GamesEditorUiState = this.updateAndGet { state ->
	if (state.gameId != gameId) {
		state
	} else {
		function(state)
	}
}

fun GamesEditorUiState.selectedFrame() = frames[scoreSheet.selection.frameIndex]

fun GamesEditorUiState.updateFrameEditor(): GamesEditorUiState {
	val selection = scoreSheet.selection
	val frame = frames[selection.frameIndex]
	val pinsDownLastRoll = if (selection.rollIndex > 0) {
		frame.deckForRoll(
			selection.rollIndex - 1,
		)
	} else {
		emptySet()
	}
	val lockedPins = if (Frame.isLastFrame(frame.properties.index)) {
		if (pinsDownLastRoll.arePinsCleared()) emptySet() else pinsDownLastRoll
	} else {
		pinsDownLastRoll
	}

	return copy(
		frameEditor = frameEditor.copy(
			lockedPins = lockedPins,
			downedPins = frame.deckForRoll(selection.rollIndex).subtract(lockedPins),
		),
	)
}

fun GamesEditorUiState.updateSelection(frameIndex: Int?, rollIndex: Int?): GamesEditorUiState {
	var selection = scoreSheet.selection.copy(
		frameIndex = frameIndex ?: scoreSheet.selection.frameIndex,
		rollIndex = rollIndex ?: scoreSheet.selection.rollIndex,
	)

	val lastAccessibleRollInFrame = frames[selection.frameIndex].lastAccessibleRollIndex
	if (lastAccessibleRollInFrame < selection.rollIndex) {
		selection = selection.copy(rollIndex = lastAccessibleRollInFrame)
	}

	val frames = if (frames.doesRollExistInFrame(selection.frameIndex, selection.rollIndex)) {
		this.frames
	} else {
		frames.toMutableList().also {
			it.guaranteeRollExists(
				frameIndex = selection.frameIndex,
				rollIndex = selection.rollIndex,
			)
		}
	}

	return copy(
		scoreSheet = scoreSheet.copy(
			selection = selection,
		),
		rollEditor = rollEditor.copy(
			didFoulRoll = frames[selection.frameIndex].rolls[selection.rollIndex].didFoul,
			selectedBall = frames[selection.frameIndex].rolls[selection.rollIndex].bowlingBall,
		),
	)
}

fun GameDetailsUiState.updateHeader(
	selection: ScoreSheetUiState.Selection,
	frames: List<FrameEdit>,
): GameDetailsUiState {
	val isManualGame = scoringMethod.scoringMethod == GameScoringMethod.MANUAL
	val isGameFinished = frames.isGameFinished()
	val isGameLocked = gameProperties.locked == GameLockState.LOCKED
	val nextGameIndex = currentGameIndex + 1

	val nextElement: NextGameEditableElement? = if (isManualGame || isGameFinished || isGameLocked) {
		val numberOfBowlers = bowlers.size
		val numberOfGames = seriesGameIds.size

		if (numberOfBowlers == 1) {
			if (nextGameIndex < numberOfGames) {
				NextGameEditableElement.Game(nextGameIndex, seriesGameIds[nextGameIndex])
			} else {
				null
			}
		} else {
			val nextBowlerIndex = (currentBowlerIndex + 1) % numberOfBowlers
			val nextBowler = bowlers[nextBowlerIndex]

			if (nextBowlerIndex == 0) {
				if (nextGameIndex < numberOfGames) {
					NextGameEditableElement.BowlerGame(nextGameIndex, nextBowler.id)
				} else {
					null
				}
			} else {
				NextGameEditableElement.Bowler(nextBowler.name, nextBowler.id)
			}
		}
	} else {
		// If the current roll isn't the last, and there are still pins standing or it's the last frame,
		// show the next roll
		if (
			!Roll.isLastRoll(selection.rollIndex) &&
			(
				Frame.isLastFrame(
					selection.frameIndex,
				) || !frames[selection.frameIndex].deckForRoll(selection.rollIndex).arePinsCleared()
				)
		) {
			NextGameEditableElement.Roll(selection.rollIndex + 1)
		} else {
			// In this case, the frame is finished
			val numberOfBowlers = bowlers.size
			val numberOfGames = seriesGameIds.size

			// If there's only one bowler, we only need to show either the next frame or next game
			if (numberOfBowlers == 1) {
				if (Frame.isLastFrame(selection.frameIndex)) {
					// If the frame is the last, show the next game if there is one
					if (nextGameIndex < numberOfGames) {
						NextGameEditableElement.Game(nextGameIndex, seriesGameIds[nextGameIndex])
					} else {
						null
					}
				} else {
					// Otherwise, show the next frame
					NextGameEditableElement.Frame(selection.frameIndex + 1)
				}
			} else {
				val nextBowlerIndex = (currentBowlerIndex + 1) % numberOfBowlers
				val nextBowler = bowlers[nextBowlerIndex]

				// If it's the last frame, we should show either the next bowler or, if there are
				// no more bowlers, the next game, or nothing
				if (Frame.isLastFrame(selection.frameIndex)) {
					// When `nextBowlerIndex` is 0, there are no bowlers following the current one.
					// Go to the next game, or show nothing
					if (nextBowlerIndex == 0) {
						if (nextGameIndex < numberOfGames) {
							NextGameEditableElement.BowlerGame(nextGameIndex, nextBowler.id)
						} else {
							null
						}
					} else {
						// Otherwise, there's another bowler to show
						NextGameEditableElement.Bowler(nextBowler.name, nextBowler.id)
					}
				} else {
					// If it's not the last frame, we should show the next bowler
					// When the next bowler's game is loaded, we'll load the correct frame at that time
					NextGameEditableElement.Bowler(nextBowler.name, nextBowler.id)
				}
			}
		}
	}

	return copy(
		header = header.copy(
			nextElement = nextElement,
		),
	)
}
