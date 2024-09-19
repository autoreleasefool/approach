package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.TeamCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamDetailsUpdateEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamEntity
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamListItem
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSummary
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TeamDao : LegacyMigratingDao<TeamEntity> {
	@Query(
		"""
			SELECT
				teams.id AS id,
				teams.name AS name,
				AVG(games.score) as average,
				MAX(series.date) as lastSeriesDate,
				COALESCE(
					(
						SELECT GROUP_CONCAT(bowlers.name, ';')
						FROM team_bowler
						LEFT JOIN bowlers
							ON bowlers.id = team_bowler.bowler_id
						WHERE team_bowler.team_id = teams.id
					),
					''
				) as members
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

	@Query(
		"""
			SELECT
				teams.id as id,
				teams.name as name
			FROM teams
			WHERE teams.id = :teamId
		""",
	)
	abstract fun getTeamSummary(teamId: TeamID): Flow<TeamSummary>

	@Query(
		"""
			SELECT
				teams.id as id,
				teams.name as name
			FROM teams
			JOIN team_series ON team_series.team_id = teams.id
			WHERE team_series.id = :teamSeriesId
		""",
	)
	abstract fun getTeamSummary(teamSeriesId: TeamSeriesID): Flow<TeamSummary>

	@Query(
		"""
			SELECT
				bowlers.id as id,
				bowlers.name as name
			FROM bowlers
			JOIN team_bowler ON team_bowler.bowler_id = bowlers.id
			WHERE team_bowler.team_id = :teamId
			ORDER BY team_bowler.position ASC
		""",
	)
	abstract fun getTeamMembers(teamId: TeamID): Flow<List<TeamMemberListItem>>

	@Insert(entity = TeamEntity::class)
	abstract suspend fun insertTeam(team: TeamCreateEntity)

	@Update(entity = TeamEntity::class)
	abstract suspend fun updateTeam(team: TeamDetailsUpdateEntity)

	@Query("DELETE FROM teams WHERE id = :id")
	abstract suspend fun deleteTeam(id: TeamID)
}
