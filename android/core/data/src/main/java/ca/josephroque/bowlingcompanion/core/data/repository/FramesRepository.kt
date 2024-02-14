package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.FrameCreate
import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.model.ScoreableFrame
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface FramesRepository {
	fun getFrames(gameId: UUID): Flow<List<FrameEdit>>
	fun getScoreableFrames(gameId: UUID): Flow<List<ScoreableFrame>>

	suspend fun insertFrames(frames: List<FrameCreate>)
	suspend fun updateFrame(frame: FrameEdit)
}
