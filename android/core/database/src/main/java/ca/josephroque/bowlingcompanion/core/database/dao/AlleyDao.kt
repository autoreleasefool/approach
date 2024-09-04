package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.AlleyCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.AlleyDetailsEntity
import ca.josephroque.bowlingcompanion.core.database.model.AlleyDetailsUpdateEntity
import ca.josephroque.bowlingcompanion.core.database.model.AlleyEntity
import ca.josephroque.bowlingcompanion.core.database.model.AlleyUpdateEntity
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.core.model.GameID
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlleyDao {
	@Query(
		"""
			SELECT 
				alleys.id AS alleyId,
				alleys.name AS name,
				alleys.material AS material,
				alleys.pin_fall AS pinFall,
				alleys.mechanism AS mechanism,
				alleys.pin_base AS pinBase
			FROM alleys
			ORDER BY alleys.name ASC
		""",
	)
	abstract fun getAlleysList(): Flow<List<AlleyListItem>>

	@Query("SELECT * FROM alleys WHERE alleys.id = :id")
	abstract fun getAlleyDetails(id: AlleyID): Flow<AlleyDetailsEntity>

	@Query(
		"""
			SELECT
				alleys.id AS id,
				alleys.name AS name,
				alleys.material AS material,
				alleys.pin_fall AS pin_fall,
				alleys.mechanism AS mechanism,
				alleys.pin_base AS pin_base
			FROM games
			JOIN series ON series.id = games.series_id
			JOIN alleys ON series.alley_id = alleys.id
			WHERE games.id = :gameId
		""",
	)
	abstract fun getGameAlleyDetails(gameId: GameID): Flow<AlleyDetailsEntity?>

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
		""",
	)
	abstract fun getAlleyUpdate(alleyId: AlleyID): Flow<AlleyUpdateEntity>

	@Insert(entity = AlleyEntity::class)
	abstract fun insertAlley(alley: AlleyCreateEntity)

	@Update(entity = AlleyEntity::class)
	abstract fun updateAlley(alley: AlleyDetailsUpdateEntity)

	@Query("DELETE FROM alleys WHERE id = :alleyId")
	abstract fun deleteAlley(alleyId: AlleyID)
}
