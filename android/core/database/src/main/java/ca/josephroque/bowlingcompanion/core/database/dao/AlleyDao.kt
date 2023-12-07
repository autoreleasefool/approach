package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.AlleyCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.AlleyDetailsUpdateEntity
import ca.josephroque.bowlingcompanion.core.database.model.AlleyEntity
import ca.josephroque.bowlingcompanion.core.database.model.AlleyUpdateEntity
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class AlleyDao {
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

	@Transaction
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
			LEFT JOIN lanes ON lanes.alley_id = alleys.id
			WHERE alleys.id = :alleyId
		"""
	)
	abstract fun getAlleyUpdate(alleyId: UUID): Flow<AlleyUpdateEntity>

	@Insert(entity = AlleyEntity::class)
	abstract fun insertAlley(alley: AlleyCreateEntity)

	@Update(entity = AlleyEntity::class)
	abstract fun updateAlley(alley: AlleyDetailsUpdateEntity)

	@Query("DELETE FROM alleys WHERE id = :alleyId")
	abstract fun deleteAlley(alleyId: UUID)
}
