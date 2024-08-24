package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.TeamCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamEntity
import ca.josephroque.bowlingcompanion.core.model.TeamListItem
import ca.josephroque.bowlingcompanion.core.model.TeamSortOrder
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TeamDao : LegacyMigratingDao<TeamEntity> {
	@Query(
		"""
			SELECT
				teams.id AS id,
				teams.name AS name,
				GROUP_CONCAT(bowlers.name, ";") AS bowlers,
				AVG(games.score) as average,
				MAX(series.date) as lastSeriesDate
			FROM teams
			LEFT JOIN team_bowler
				ON team_bowler.team_id = teams.id
			LEFT JOIN bowlers
				ON bowlers.id = team_bowler.bowler_id
			LEFT JOIN leagues 
				ON leagues.bowler_id = bowlers.id
				AND (leagues.exclude_from_statistics = 'INCLUDE' OR leagues.exclude_from_statistics IS NULL)
				AND leagues.archived_on IS NULL
			LEFT JOIN series
				ON series.league_id = leagues.id
				AND (series.exclude_from_statistics = 'INCLUDE' OR series.exclude_from_statistics IS NULL)
				AND series.archived_on IS NULL
			LEFT JOIN games
				ON games.series_id = series.id
				AND (games.exclude_from_statistics = 'INCLUDE' OR games.exclude_from_statistics IS NULL)
				AND (games.score > 0 OR games.score IS NULL)
				AND games.archived_on IS NULL
			GROUP BY teams.id
			ORDER BY
				CASE WHEN :sortOrder = 'MOST_RECENTLY_USED' THEN lastSeriesDate END DESC,
				CASE WHEN :sortOrder = 'ALPHABETICAL' THEN teams.name END ASC,
				bowlers.name
		""",
	)
	abstract fun getTeamList(sortOrder: TeamSortOrder): Flow<List<TeamListItem>>

	@Insert(entity = TeamEntity::class)
	abstract suspend fun insertTeam(team: TeamCreateEntity)

	@Query("DELETE FROM teams WHERE id = :id")
	abstract suspend fun deleteTeam(id: UUID)
}
