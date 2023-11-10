package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.GearCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.GearEntity
import ca.josephroque.bowlingcompanion.core.database.model.GearUpdateEntity
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class GearDao {
	@Query(
		"""
			SELECT
				gear.id AS id,
				gear.name AS name,
				gear.kind AS kind,
				gear.avatar as avatar,
				owner.name AS ownerName
			FROM gear
			JOIN bowler_preferred_gear
				ON gear.id = bowler_preferred_gear.gear_id
				AND bowler_preferred_gear.gear_id = :bowlerId
			LEFT JOIN bowlers AS owner 
				ON gear.owner_id = owner.id
			ORDER BY gear.name
		"""
	)
	abstract fun getBowlerPreferredGear(bowlerId: UUID): Flow<List<GearListItem>>

	@Query(
		"""
			SELECT
				gear.id AS id,
				gear.name AS name,
				gear.kind AS kind,
				gear.avatar as avatar,
				owner.name AS ownerName
			FROM gear
			LEFT JOIN bowlers AS owner
				ON gear.owner_id = owner.id
			WHERE (:kind IS NULL OR gear.kind = :kind)
			ORDER BY gear.name
		"""
	)
	abstract fun getGearList(kind: GearKind? = null): Flow<List<GearListItem>>

	@Query(
		"""
			SELECT
				gear.id AS id,
				gear.name AS name,
				gear.kind AS kind,
				gear.avatar AS avatar,
				gear.owner_id AS ownerId
			FROM gear WHERE gear.id = :id
		"""
	)
	abstract fun getGearUpdate(id: UUID): Flow<GearUpdateEntity>

	@Insert(entity = GearEntity::class)
	abstract fun insertGear(gear: GearCreateEntity)

	@Update(entity = GearEntity::class)
	abstract fun updateGear(gear: GearUpdateEntity)

	@Query("DELETE FROM gear WHERE id = :gearId")
	abstract fun deleteGear(gearId: UUID)
}