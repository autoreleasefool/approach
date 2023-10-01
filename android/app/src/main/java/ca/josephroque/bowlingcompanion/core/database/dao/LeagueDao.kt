package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueUpdate
import ca.josephroque.bowlingcompanion.core.model.LeagueDetails
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class LeagueDao: BaseDao<LeagueEntity> {
	@Query("""
		SELECT 
		 id,
		 name,
		 recurrence,
		 number_of_games AS numberOfGames,
		 additional_pin_fall AS additionalPinFall,
		 additional_games AS additionalGames,
		 exclude_from_statistics AS excludeFromStatistics
		FROM leagues 
		WHERE id = :leagueId
	""")
	abstract fun getLeagueDetails(leagueId: UUID): Flow<LeagueDetails>

	@Query(
		"""
			SELECT
				leagues.id AS id,
				leagues.name AS name,
				leagues.recurrence AS recurrence,
				MAX(series.date) AS lastSeriesDate,
				AVG(games.score) AS average
			FROM leagues
			LEFT JOIN series
				ON series.league_id = leagues.id
				AND (series.exclude_from_statistics = "INCLUDE" OR series.exclude_from_statistics IS NULL)
			LEFT JOIN games
				ON games.series_id = series.id
				AND (games.exclude_from_statistics = "INCLUDE" OR games.exclude_from_statistics IS NULL)
				AND (games.score > 0 OR games.score IS NULL)
			WHERE leagues.bowler_id = :bowlerId
			GROUP BY leagues.id
		"""
	)
	abstract fun getLeagueAverages(bowlerId: UUID): Flow<List<LeagueListItem>>

	@Insert(entity = LeagueEntity::class)
	abstract fun insertLeague(league: LeagueCreate)

	@Update(entity = LeagueEntity::class)
	abstract fun updateLeague(league: LeagueUpdate)

	@Query("DELETE FROM leagues WHERE id = :leagueId")
	abstract fun deleteLeague(leagueId: UUID)
}