package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.ScoringGame
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface ScoresRepository {
	fun getScore(gameId: UUID): Flow<ScoringGame>
	suspend fun getHighestScorePossible(gameId: UUID): Int
}
