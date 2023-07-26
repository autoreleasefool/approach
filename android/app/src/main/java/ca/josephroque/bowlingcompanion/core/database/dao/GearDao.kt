package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.BowlerWithGear
import ca.josephroque.bowlingcompanion.core.database.model.GearEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface GearDao {
	@Query(value = "SELECT * from gear")
	fun getGearEntities(): Flow<List<GearEntity>>

	@Query(value = """
		SELECT * from bowlers
		WHERE id = :ownerId
	""")
	fun getOwnerGear(ownerId: UUID): Flow<BowlerWithGear>
}