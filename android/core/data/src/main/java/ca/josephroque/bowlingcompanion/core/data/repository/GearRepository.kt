package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GearCreate
import ca.josephroque.bowlingcompanion.core.model.GearDetails
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.GearUpdate
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface GearRepository {
	fun getBowlerPreferredGear(bowlerId: BowlerID): Flow<List<GearListItem>>
	fun getGameGear(gameId: GameID): Flow<List<GearListItem>>

	fun getRecentlyUsedGear(kind: GearKind? = null, limit: Int): Flow<List<GearListItem>>
	fun getGearList(kind: GearKind? = null): Flow<List<GearListItem>>

	fun getGearUpdate(id: UUID): Flow<GearUpdate>
	fun getGearDetails(id: UUID): Flow<GearDetails>

	suspend fun setBowlerPreferredGear(bowlerId: BowlerID, gear: Set<UUID>)
	suspend fun setGameGear(gameId: GameID, gear: Set<UUID>)

	suspend fun insertGear(gear: GearCreate)
	suspend fun updateGear(gear: GearUpdate)
	suspend fun deleteGear(id: UUID)
}
