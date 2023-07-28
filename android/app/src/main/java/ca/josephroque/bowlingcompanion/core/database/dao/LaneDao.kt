package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ca.josephroque.bowlingcompanion.core.database.relationship.AlleyWithLanes
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface LaneDao {
	@Transaction
	@Query(value = """
		SELECT * from alleys
		WHERE id = :alleyId
	""")
	abstract fun getAlleyLanes(alleyId: UUID): Flow<AlleyWithLanes>
}