package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LanesRepository {
	fun getLanes(ids: List<UUID>): Flow<List<LaneListItem>>

	suspend fun insertLanes(lanes: List<LaneListItem>)
	suspend fun setAlleyLanes(alleyId: UUID, lanes: List<LaneListItem>)
}