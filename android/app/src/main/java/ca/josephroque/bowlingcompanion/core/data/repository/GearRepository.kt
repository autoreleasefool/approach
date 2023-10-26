package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.model.GearCreate
import ca.josephroque.bowlingcompanion.core.database.model.GearUpdate
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface GearRepository {
	fun getBowlerPreferredGear(bowlerId: BowlerID): Flow<List<GearListItem>>
	fun getRecentlyUsedGear(limit: Int): Flow<List<GearListItem>>

	suspend fun insertGear(gear: GearCreate)
	suspend fun updateGear(gear: GearUpdate)
	suspend fun deleteGear(id: UUID)
}