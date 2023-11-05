package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.model.ScoreableFrame
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface FramesRepository {
	fun getFrames(gameId: UUID): Flow<List<FrameEdit>>
	fun getScoreableFrames(gameId: UUID): Flow<List<ScoreableFrame>>

	suspend fun updateFrame(frame: FrameEdit)
}