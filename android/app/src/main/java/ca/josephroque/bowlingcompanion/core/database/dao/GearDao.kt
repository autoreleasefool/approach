package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.GearCreate
import ca.josephroque.bowlingcompanion.core.database.model.GearEntity
import ca.josephroque.bowlingcompanion.core.database.model.GearUpdate
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class GearDao: BaseDao<GearEntity> {
	@Query(
		"""
			SELECT
				gear.id AS id,
				gear.name AS name,
				gear.kind AS kind,
				owner.name AS ownerName
			FROM gear
			JOIN bowler_preferred_gear
				ON gear.id = bowler_preferred_gear.gear_id
				AND bowler_preferred_gear.gear_id = :bowlerId
			JOIN bowlers AS owner 
				ON gear.owner_id = owner.id
			ORDER BY gear.name
		"""
	)
	abstract fun getBowlerPreferredGear(bowlerId: BowlerID): Flow<List<GearListItem>>

	@Query(
		"""
			SELECT
				gear.id AS id,
				gear.name AS name,
				gear.kind AS kind,
				owner.name AS ownerName
			FROM gear
			JOIN bowlers AS owner
				ON gear.owner_id = owner.id
			ORDER BY gear.name
		"""
	)
	abstract fun getGearList(): Flow<List<GearListItem>>

	@Insert(entity = GearEntity::class)
	abstract fun insertGear(gear: GearCreate)

	@Update(entity = GearEntity::class)
	abstract fun updateGear(gear: GearUpdate)

	@Query("DELETE FROM gear WHERE id = :gearId")
	abstract fun deleteGear(gearId: UUID)
}