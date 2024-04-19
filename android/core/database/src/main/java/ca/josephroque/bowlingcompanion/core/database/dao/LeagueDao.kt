package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.LeagueCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueUpdateEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedLeague
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueDetails
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
abstract class LeagueDao : LegacyMigratingDao<LeagueEntity> {
	@Query(
		"""
			SELECT
				bowlers.id AS id,
				bowlers.name AS name
			FROM leagues
			JOIN bowlers ON leagues.bowler_id = bowlers.id
			WHERE leagues.id = :leagueId
		""",
	)
	abstract fun getLeagueBowler(leagueId: UUID): Flow<BowlerSummary>

	@Query(
		"""
			SELECT
				leagues.id AS id,
				leagues.name AS name
			FROM leagues
			WHERE leagues.id = :leagueId

		""",
	)
	abstract fun getLeagueSummary(leagueId: UUID): Flow<LeagueSummary>

	@Query(
		"""
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
	""",
	)
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
				AND (series.exclude_from_statistics = 'INCLUDE' OR series.exclude_from_statistics IS NULL)
				AND series.archived_on IS NULL
			LEFT JOIN games
				ON games.series_id = series.id
				AND (games.exclude_from_statistics = 'INCLUDE' OR games.exclude_from_statistics IS NULL)
				AND (games.score > 0 OR games.score IS NULL)
				AND games.archived_on IS NULL
			WHERE 
				leagues.bowler_id = :bowlerId 
				AND leagues.archived_on IS NULL
				AND (leagues.recurrence = :recurrence OR :recurrence IS NULL)
			GROUP BY leagues.id
			ORDER BY leagues.name
		""",
	)
	abstract fun getLeagueAverages(
		bowlerId: UUID,
		recurrence: LeagueRecurrence?,
	): Flow<List<LeagueListItem>>

	@Query(
		"""
			SELECT
				leagues.id AS id,
				leagues.name AS name,
				leagues.archived_on AS archivedOn,
				bowlers.name AS bowlerName,
				COUNT(DISTINCT series.id) AS numberOfSeries,
				COUNT(DISTINCT games.id) AS numberOfGames
			FROM leagues
			JOIN bowlers ON bowlers.id = leagues.bowler_id
			LEFT JOIN series on series.league_id = leagues.id
			LEFT JOIN games on games.series_id = series.id
			WHERE leagues.archived_on IS NOT NULL
			GROUP BY leagues.id
			ORDER BY leagues.archived_on DESC
		""",
	)
	abstract fun getArchivedLeagues(): Flow<List<ArchivedLeague>>

	@Insert(entity = LeagueEntity::class)
	abstract fun insertLeague(league: LeagueCreateEntity)

	@Update(entity = LeagueEntity::class)
	abstract fun updateLeague(league: LeagueUpdateEntity)

	@Query("UPDATE leagues SET archived_on = :archivedOn WHERE id = :leagueId")
	abstract fun archiveLeague(leagueId: UUID, archivedOn: Instant)

	@Query("UPDATE leagues SET archived_on = NULL WHERE id = :leagueId")
	abstract fun unarchiveLeague(leagueId: UUID)
}
