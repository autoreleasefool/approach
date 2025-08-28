package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.ScoringGame
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import kotlinx.coroutines.flow.Flow

interface ScoresRepository {
	fun getScore(gameId: GameID): Flow<ScoringGame>
	fun getScores(seriesId: SeriesID): Flow<List<ScoringGame>>
	suspend fun getHighestScorePossible(gameId: GameID): Int
}
