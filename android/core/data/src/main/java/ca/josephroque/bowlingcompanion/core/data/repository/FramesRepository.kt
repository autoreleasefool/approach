package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.FrameCreate
import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.ScoreableFrame
import kotlinx.coroutines.flow.Flow

interface FramesRepository {
	fun getFrames(gameId: GameID): Flow<List<FrameEdit>>
	fun getScoreableFrames(gameId: GameID): Flow<List<ScoreableFrame>>

	suspend fun insertFrames(frames: List<FrameCreate>)
	suspend fun updateFrame(frame: FrameEdit)
}
