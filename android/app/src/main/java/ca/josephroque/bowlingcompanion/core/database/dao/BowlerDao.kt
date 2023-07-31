package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class BowlerDao: BaseDao<BowlerEntity> {
	@Query("SELECT * FROM bowlers WHERE kind = :kind")
	abstract fun getBowlers(
		kind: BowlerKind = BowlerKind.PLAYABLE
	): Flow<List<BowlerEntity>>

	@Query("SELECT * FROM bowlers WHERE id = :bowlerId")
	abstract fun getBowler(bowlerId: UUID): Flow<BowlerEntity>

	@Update(entity = BowlerEntity::class)
	abstract fun updateBowler(bowler: BowlerUpdate)

	@Query("DELETE FROM bowlers WHERE id = :bowlerId")
	abstract fun deleteBowler(bowlerId: UUID)
}