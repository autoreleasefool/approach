package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.ScoringGame
import kotlinx.coroutines.flow.Flow

interface ScoresRepository {
	fun getScore(gameId: GameID): Flow<ScoringGame>
	suspend fun getHighestScorePossible(gameId: GameID): Int
}
