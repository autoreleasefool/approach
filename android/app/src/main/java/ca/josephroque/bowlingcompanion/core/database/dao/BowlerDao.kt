package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface BowlerDao {
	@Query(value = "SELECT * FROM bowlers")
	fun getBowlerEntities(): Flow<List<BowlerEntity>>

	@Query(
		value = """
			SELECT * FROM bowlers
			WHERE id = :bowlerId
		"""
	)
	fun getBowler(bowlerId: UUID): Flow<BowlerEntity>

	@Upsert
	suspend fun upsertBowlers(entities: List<BowlerEntity>)

	@Query("DELETE FROM bowlers WHERE id = :id")
	suspend fun deleteBowler(id: UUID)
}