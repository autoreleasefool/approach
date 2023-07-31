package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.AlleyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlleyDao {
	@Query(value = "SELECT * from alleys")
	fun getAlleyEntities(): Flow<List<AlleyEntity>>
}