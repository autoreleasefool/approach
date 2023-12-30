package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.FrameDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.FrameEditEntity
import ca.josephroque.bowlingcompanion.core.database.model.ScoreableFrameEntity
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.FrameCreate
import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.model.ScoreableFrame
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class OfflineFirstFramesRepository @Inject constructor(
	private val frameDao: FrameDao,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): FramesRepository {
	override fun getFrames(gameId: UUID): Flow<List<FrameEdit>> =
		frameDao.getFrames(gameId).map { it.map(FrameEditEntity::asModel) }

	override fun getScoreableFrames(gameId: UUID): Flow<List<ScoreableFrame>> =
		frameDao.getScoreableFrames(gameId).map { it.map(ScoreableFrameEntity::asModel) }

	override suspend fun insertFrames(frames: List<FrameCreate>) = withContext(ioDispatcher) {
		transactionRunner {
			frameDao.insertFrames(frames.map(FrameCreate::asEntity))
		}
	}

	override suspend fun updateFrame(frame: FrameEdit) = withContext(ioDispatcher) {
		frameDao.updateFrame(frame.asEntity())
	}
}