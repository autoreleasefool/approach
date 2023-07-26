package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ca.josephroque.bowlingcompanion.core.database.model.SeriesWithGames
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface GameDao {
	@Transaction
	@Query("""
		SELECT * from series
		WHERE id = :seriesId
	""")
	abstract fun getSeriesGames(seriesId: UUID): Flow<SeriesWithGames>
}