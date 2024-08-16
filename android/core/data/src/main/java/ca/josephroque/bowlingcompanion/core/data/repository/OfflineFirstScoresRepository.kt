package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.ScoringGame
import ca.josephroque.bowlingcompanion.core.scoring.ScoreKeeper
import ca.josephroque.bowlingcompanion.core.scoring.ScoreKeeperInput
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class OfflineFirstScoresRepository @Inject constructor(
	private val gamesRepository: GamesRepository,
	private val framesRepository: FramesRepository,
	private val scoreKeeper: ScoreKeeper,
) : ScoresRepository {
	override fun getScore(gameId: UUID): Flow<ScoringGame> = combine(
		gamesRepository.getGameIndex(gameId),
		framesRepository.getScoreableFrames(gameId),
	) { gameIndex, frames ->
		val scoringFrames = scoreKeeper.calculateScore(ScoreKeeperInput.fromFrames(frames))
		ScoringGame(id = gameId, index = gameIndex, frames = scoringFrames)
	}

	override suspend fun getHighestScorePossible(gameId: UUID): Int {
		val frames = framesRepository.getScoreableFrames(gameId).first()
		return scoreKeeper.calculateHighestScorePossible(
			ScoreKeeperInput.fromFrames(frames),
		)
	}
}
