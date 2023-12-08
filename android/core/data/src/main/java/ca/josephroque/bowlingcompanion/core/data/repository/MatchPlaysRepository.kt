package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.MatchPlayCreate
import ca.josephroque.bowlingcompanion.core.model.MatchPlayUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface MatchPlaysRepository {
	fun getMatchPlay(gameId: UUID): Flow<MatchPlayUpdate?>

	suspend fun insertMatchPlay(matchPlay: MatchPlayCreate)
	suspend fun updateMatchPlay(matchPlay: MatchPlayUpdate)
	suspend fun deleteMatchPlay(matchPlayId: UUID)
}