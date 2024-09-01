package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.MatchPlayCreate
import ca.josephroque.bowlingcompanion.core.model.MatchPlayUpdate
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface MatchPlaysRepository {
	fun getMatchPlay(gameId: GameID): Flow<MatchPlayUpdate?>

	suspend fun insertMatchPlay(matchPlay: MatchPlayCreate)
	suspend fun updateMatchPlay(matchPlay: MatchPlayUpdate)
	suspend fun deleteMatchPlay(matchPlayId: UUID)
}
