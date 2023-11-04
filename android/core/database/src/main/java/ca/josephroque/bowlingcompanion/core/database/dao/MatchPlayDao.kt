package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.MatchPlayEntity
import java.util.UUID

@Dao
abstract class MatchPlayDao: LegacyMigratingDao<MatchPlayEntity> {
	@Query("SELECT * FROM match_plays WHERE game_id IN (:gameIds)")
	abstract suspend fun getMatchPlaysForGames(gameIds: Collection<UUID>): List<MatchPlayEntity>
}