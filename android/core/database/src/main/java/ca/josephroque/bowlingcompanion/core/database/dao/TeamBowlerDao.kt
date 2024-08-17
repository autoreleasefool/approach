package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import ca.josephroque.bowlingcompanion.core.database.model.TeamBowlerCrossRef

@Dao
abstract class TeamBowlerDao : LegacyMigratingDao<TeamBowlerCrossRef> {
	@Insert
	abstract fun setTeamBowlers(teamBowlers: List<TeamBowlerCrossRef>)
}
