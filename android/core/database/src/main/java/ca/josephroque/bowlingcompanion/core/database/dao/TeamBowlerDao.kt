package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.TeamBowlerCrossRef
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TeamBowlerDao : LegacyMigratingDao<TeamBowlerCrossRef> {
	@Query(
		"""
			SELECT
				bowlers.id as id,
				bowlers.name as name
			FROM bowlers
			WHERE bowlers.id IN (:ids)
			ORDER BY bowlers.name
		""",
	)
	abstract fun getTeamMembers(ids: List<BowlerID>): Flow<List<TeamMemberListItem>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	abstract fun setTeamBowlers(teamBowlers: List<TeamBowlerCrossRef>)

	@Query("DELETE FROM team_bowler WHERE team_id = :teamId")
	abstract fun deleteTeamBowlers(teamId: TeamID)
}
