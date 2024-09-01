package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface LanesRepository {
	fun getLanes(ids: List<UUID>): Flow<List<LaneListItem>>
	fun getAlleyLanes(alleyId: UUID): Flow<List<LaneListItem>>
	fun getGameLanes(gameId: GameID): Flow<List<LaneListItem>>

	suspend fun insertLanes(lanes: List<LaneListItem>)
	suspend fun setAlleyLanes(alleyId: UUID, lanes: List<LaneListItem>)
}
