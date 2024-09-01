package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GearCreate
import ca.josephroque.bowlingcompanion.core.model.GearDetails
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.GearUpdate
import kotlinx.coroutines.flow.Flow

interface GearRepository {
	fun getBowlerPreferredGear(bowlerId: BowlerID): Flow<List<GearListItem>>
	fun getGameGear(gameId: GameID): Flow<List<GearListItem>>

	fun getRecentlyUsedGear(kind: GearKind? = null, limit: Int): Flow<List<GearListItem>>
	fun getGearList(kind: GearKind? = null): Flow<List<GearListItem>>

	fun getGearUpdate(id: GearID): Flow<GearUpdate>
	fun getGearDetails(id: GearID): Flow<GearDetails>

	suspend fun setBowlerPreferredGear(bowlerId: BowlerID, gear: Set<GearID>)
	suspend fun setGameGear(gameId: GameID, gear: Set<GearID>)

	suspend fun insertGear(gear: GearCreate)
	suspend fun updateGear(gear: GearUpdate)
	suspend fun deleteGear(id: GearID)
}
