package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.AlleyCreate
import ca.josephroque.bowlingcompanion.core.database.model.AlleyEntity
import ca.josephroque.bowlingcompanion.core.database.model.AlleyUpdate
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class AlleyDao: BaseDao<AlleyEntity> {
	@Query(
		"""
			SELECT 
				alleys.id AS id,
				alleys.name AS name,
				alleys.material AS material,
				alleys.pin_fall AS pinFall,
				alleys.mechanism AS mechanism,
				alleys.pin_base AS pinBase
			FROM alleys
			ORDER BY alleys.name
		"""
	)
	abstract fun getAlleysList(): Flow<List<AlleyListItem>>

	@Insert(entity = AlleyEntity::class)
	abstract fun insertAlley(alley: AlleyCreate)

	@Update(entity = AlleyEntity::class)
	abstract fun updateAlley(alley: AlleyUpdate)

	@Query("DELETE FROM alleys WHERE id = :alleyId")
	abstract fun deleteAlley(alleyId: UUID)
}
