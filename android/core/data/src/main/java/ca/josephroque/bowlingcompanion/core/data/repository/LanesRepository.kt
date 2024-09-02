package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import kotlinx.coroutines.flow.Flow

interface LanesRepository {
	fun getLanes(ids: List<LaneID>): Flow<List<LaneListItem>>
	fun getAlleyLanes(alleyId: AlleyID): Flow<List<LaneListItem>>
	fun getGameLanes(gameId: GameID): Flow<List<LaneListItem>>

	suspend fun insertLanes(lanes: List<LaneListItem>)
	suspend fun setAlleyLanes(alleyId: AlleyID, lanes: List<LaneListItem>)
}
