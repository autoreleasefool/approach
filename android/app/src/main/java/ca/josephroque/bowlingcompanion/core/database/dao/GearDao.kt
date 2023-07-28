package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ca.josephroque.bowlingcompanion.core.database.relationship.BowlerWithGear
import ca.josephroque.bowlingcompanion.core.database.model.GearEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface GearDao {
	@Query(value = "SELECT * from gear")
	fun getGearEntities(): Flow<List<GearEntity>>

	@Transaction
	@Query(value = """
		SELECT * from bowlers
		WHERE id = :ownerId
	""")
	fun getOwnerGear(ownerId: UUID): Flow<BowlerWithGear>
}