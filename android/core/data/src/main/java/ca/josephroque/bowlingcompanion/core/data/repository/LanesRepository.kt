package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.model.LaneCreate
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LanesRepository {
	fun alleyLanes(alleyId: UUID): Flow<List<LaneListItem>>

	suspend fun overwriteAlleyLanes(alleyId: UUID, lanes: List<LaneCreate>)
}