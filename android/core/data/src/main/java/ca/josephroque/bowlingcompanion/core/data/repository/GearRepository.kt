package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.GearCreate
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.GearUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface GearRepository {
	fun getBowlerPreferredGear(bowlerId: UUID): Flow<List<GearListItem>>
	fun getRecentlyUsedGear(limit: Int): Flow<List<GearListItem>>
	fun getGearList(kind: GearKind? = null): Flow<List<GearListItem>>

	fun getGearUpdate(id: UUID): Flow<GearUpdate>

	suspend fun insertGear(gear: GearCreate)
	suspend fun updateGear(gear: GearUpdate)
	suspend fun deleteGear(id: UUID)
}