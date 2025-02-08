package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ca.josephroque.bowlingcompanion.core.database.model.BowlerCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.BowlerUpdateEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedBowler
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.BowlerSortOrder
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesBowlerSummary
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
abstract class BowlerDao : LegacyMigratingDao<BowlerEntity> {
	@Query(
		"""
			SELECT
				bowlers.id AS id,
				bowlers.name AS name
			FROM bowlers
			WHERE bowlers.id = :bowlerId
		""",
	)
	abstract fun getBowlerSummary(bowlerId: BowlerID): Flow<BowlerSummary>

	@Query(
		"""
			SELECT
				series.id AS seriesId,
			 	bowlers.id AS id,
			 	bowlers.name AS name
			FROM series
			JOIN leagues ON leagues.id = series.league_id
			JOIN bowlers ON bowlers.id = leagues.bowler_id
			WHERE series.id IN (:series)
		""",
	)
	abstract fun getSeriesBowlers(series: List<SeriesID>): Flow<List<SeriesBowlerSummary>>

	@Query(
		"""
			SELECT
				bowlers.id as id,
				bowlers.name as name,
				bowlers.kind as kind
			FROM bowlers 
			WHERE id = :bowlerId
		""",
	)
	abstract fun getBowlerDetails(bowlerId: BowlerID): Flow<BowlerDetails>

	@Query(
		"""
			SELECT
				bowlers.id AS id,
				bowlers.name AS name,
				AVG(games.score) as average,
				MAX(series.date) as lastSeriesDate
			FROM bowlers
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
			WHERE (:kind IS NULL OR bowlers.kind = :kind) AND bowlers.archived_on IS NULL
			GROUP BY bowlers.id
			ORDER BY
				CASE WHEN :sortOrder = 'MOST_RECENTLY_USED' THEN lastSeriesDate END DESC,
				CASE WHEN :sortOrder = 'ALPHABETICAL' THEN bowlers.name END ASC
		""",
	)
	abstract fun getBowlersList(kind: BowlerKind?, sortOrder: BowlerSortOrder): Flow<List<BowlerListItem>>

	@Query(
		"""
			SELECT
				bowlers.id AS id,
				bowlers.name AS name,
				bowlers.kind AS kind
			FROM bowlers
			WHERE bowlers.archived_on IS NULL
			ORDER BY bowlers.name ASC
		""",
	)
	abstract fun getOpponentsList(): Flow<List<OpponentListItem>>

	@Query(
		"""
			SELECT
				bowlers.id AS id,
				bowlers.name AS name,
				bowlers.archived_on AS archivedOn,
				COUNT(DISTINCT leagues.id) AS numberOfLeagues,
				COUNT(DISTINCT series.id) AS numberOfSeries,
				COUNT(DISTINCT games.id) AS numberOfGames
			FROM bowlers
			LEFT JOIN leagues on leagues.bowler_id = bowlers.id
			LEFT JOIN series on series.league_id = leagues.id
			LEFT JOIN games on games.series_id = series.id
			WHERE bowlers.archived_on IS NOT NULL
			GROUP BY bowlers.id
			ORDER BY bowlers.archived_on DESC
		""",
	)
	abstract fun getArchivedBowlers(): Flow<List<ArchivedBowler>>

	@Insert(entity = BowlerEntity::class)
	abstract fun insertBowler(bowler: BowlerCreateEntity)

	@Update(entity = BowlerEntity::class)
	abstract fun updateBowler(bowler: BowlerUpdateEntity)

	@Update
	abstract fun updateBowlerEntity(bowler: BowlerEntity)

	@Query("UPDATE bowlers SET archived_on = :archivedOn WHERE id IN (:bowlerIds)")
	abstract fun archiveBowlers(bowlerIds: List<BowlerID>, archivedOn: Instant)

	@Query("UPDATE bowlers SET archived_on = NULL WHERE id = :bowlerId")
	abstract fun unarchiveBowler(bowlerId: BowlerID)
}
