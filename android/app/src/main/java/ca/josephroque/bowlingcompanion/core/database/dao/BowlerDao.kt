package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BowlerDao: BaseDao<BowlerEntity> {
	@Query(
		"""
			SELECT
				bowlers.id as id,
				bowlers.name as name
			FROM bowlers 
			WHERE id = :bowlerId
		"""
	)
	abstract fun getBowlerDetails(bowlerId: BowlerID): Flow<BowlerDetails>

	@Query(
		"""
			SELECT
				bowlers.id AS id,
				bowlers.name AS name,
				AVG(games.score) as average
			FROM bowlers
			LEFT JOIN leagues
				ON leagues.bowler_id = bowlers.id
				AND (leagues.exclude_from_statistics = "INCLUDE" OR leagues.exclude_from_statistics IS NULL)
			LEFT JOIN series
				ON series.league_id = leagues.id
				AND (series.exclude_from_statistics = "INCLUDE" OR series.exclude_from_statistics IS NULL)
			LEFT JOIN games
				ON games.series_id = series.id
				AND (games.exclude_from_statistics = "INCLUDE" OR games.exclude_from_statistics IS NULL)
				AND (games.score > 0 OR games.score IS NULL)
			WHERE bowlers.kind = "PLAYABLE"
			GROUP BY bowlers.id
			ORDER BY bowlers.name
		"""
	)
	abstract fun getBowlersList(): Flow<List<BowlerListItem>>

	@Query(
		"""
			SELECT
				bowlers.id AS id,
				bowlers.name AS name,
				bowlers.kind AS kind
			FROM bowlers
			ORDER BY bowlers.name
		"""
	)
	abstract fun getOpponentsList(): Flow<List<OpponentListItem>>

	@Insert(entity = BowlerEntity::class)
	abstract fun insertBowler(bowler: BowlerCreate)

	@Update(entity = BowlerEntity::class)
	abstract fun updateBowler(bowler: BowlerUpdate)

	@Query("DELETE FROM bowlers WHERE id = :bowlerId")
	abstract fun deleteBowler(bowlerId: BowlerID)
}