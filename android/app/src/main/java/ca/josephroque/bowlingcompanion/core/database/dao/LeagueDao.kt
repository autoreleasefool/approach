package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueWithAverage
import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class LeagueDao: BaseDao<LeagueEntity> {
	@Query(
		"""
			SELECT
				leagues.id as id,
				leagues.name as name,
				leagues.recurrence as recurrence,
				MAX(series.date) AS lastSeriesDate,
				AVG(games.score) as average
			FROM leagues
			LEFT JOIN series
				ON series.league_id = leagues.id
				AND (series.exclude_from_statistics = "INCLUDE" OR series.exclude_from_statistics IS NULL)
			LEFT JOIN games
				ON games.series_id = series.id
				AND (games.exclude_from_statistics = "INCLUDE" OR games.exclude_from_statistics IS NULL)
				AND (games.score > 0 OR games.score is NULL)
			GROUP BY leagues.id
		"""
	)
	abstract fun getLeagueAverages(): Flow<List<LeagueWithAverage>>

	@Insert(entity = LeagueEntity::class)
	abstract fun insertLeague(league: LeagueCreate)

	@Update(entity = LeagueEntity::class)
	abstract fun updateLeague(league: LeagueUpdate)

	@Query("DELETE FROM leagues WHERE id = :leagueId")
	abstract fun deleteLeague(leagueId: UUID)
}