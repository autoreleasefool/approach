package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.FrameDao
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.database.model.ScoreableFrameEntity
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.ScoringGame
import ca.josephroque.bowlingcompanion.core.scoring.ScoreKeeper
import ca.josephroque.bowlingcompanion.core.scoring.ScoreKeeperInput
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class OfflineFirstScoresRepository @Inject constructor(
	private val gameDao: GameDao,
	private val frameDao: FrameDao,
	private val scoreKeeper: ScoreKeeper,
) : ScoresRepository {
	override fun getScore(gameId: GameID): Flow<ScoringGame> = combine(
		gameDao.getGameIndex(gameId),
		frameDao
			.getScoreableFrames(gameId)
			.map { it.map(ScoreableFrameEntity::asModel) },
	) { gameIndex, frames ->
		val scoringFrames = scoreKeeper.calculateScore(ScoreKeeperInput.fromFrames(frames))
		ScoringGame(id = gameId, index = gameIndex, frames = scoringFrames)
	}

	override suspend fun getHighestScorePossible(gameId: GameID): Int {
		val frames = frameDao.getScoreableFrames(gameId).first().map(ScoreableFrameEntity::asModel)
		return scoreKeeper.calculateHighestScorePossible(
			ScoreKeeperInput.fromFrames(frames),
		)
	}
}
