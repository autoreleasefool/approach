package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.BowlerWithAverage
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
		"""
	)
	abstract fun getBowlerAverages(): Flow<List<BowlerWithAverage>>

	@Update(entity = BowlerEntity::class)
	abstract fun updateBowler(bowler: BowlerUpdate)

	@Query("DELETE FROM bowlers WHERE id = :bowlerId")
	abstract fun deleteBowler(bowlerId: UUID)
}