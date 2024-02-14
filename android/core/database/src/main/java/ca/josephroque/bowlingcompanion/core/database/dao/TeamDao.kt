package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import ca.josephroque.bowlingcompanion.core.database.model.TeamEntity

@Dao
abstract class TeamDao : LegacyMigratingDao<TeamEntity>
