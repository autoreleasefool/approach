package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ca.josephroque.bowlingcompanion.core.database.relationship.LeagueWithSeries
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface SeriesDao {
	@Transaction
	@Query("""
		SELECT * FROM leagues
		WHERE id = :leagueId
	""")
	fun getLeagueSeries(leagueId: UUID): Flow<LeagueWithSeries>
}