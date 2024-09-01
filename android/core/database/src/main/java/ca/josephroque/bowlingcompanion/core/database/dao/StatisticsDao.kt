package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomWarnings
import androidx.sqlite.db.SimpleSQLiteQuery
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.TrackableFilterSourceSummariesEntity
import ca.josephroque.bowlingcompanion.core.database.model.TrackableFrameEntity
import ca.josephroque.bowlingcompanion.core.database.model.TrackableGameEntity
import ca.josephroque.bowlingcompanion.core.database.model.TrackableSeriesEntity
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import java.util.UUID

@Dao
interface StatisticsDao {
	@Query(
		"""
			SELECT
				bowlers.id AS bowler_id,
				bowlers.name AS bowler_name
			FROM bowlers
			WHERE bowlers.id = :bowlerId
		""",
	)
	@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
	fun getBowlerSourceDetails(bowlerId: BowlerID): TrackableFilterSourceSummariesEntity

	@Query(
		"""
			SELECT
				bowlers.id AS bowler_id,
				bowlers.name AS bowler_name,
				leagues.id AS league_id,
				leagues.name AS league_name
			FROM leagues
			JOIN bowlers ON bowlers.id = leagues.bowler_id
			WHERE leagues.id = :leagueId
		""",
	)
	@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
	fun getLeagueSourceDetails(leagueId: LeagueID): TrackableFilterSourceSummariesEntity

	@Query(
		"""
			SELECT
				bowlers.id AS bowler_id,
				bowlers.name AS bowler_name,
				leagues.id AS league_id,
				leagues.name AS league_name,
				series.id AS series_id,
				series.date AS series_date
			FROM series
			JOIN leagues ON leagues.id = series.league_id
			JOIN bowlers ON bowlers.id = leagues.bowler_id
			WHERE series.id = :seriesId 
		""",
	)
	@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
	fun getSeriesSourceDetails(seriesId: SeriesID): TrackableFilterSourceSummariesEntity

	@Query(
		"""
			SELECT
				bowlers.id AS bowler_id,
				bowlers.name AS bowler_name,
				leagues.id AS league_id,
				leagues.name AS league_name,
				series.id AS series_id,
				series.date AS series_date,
				games.id AS game_id,
				games.`index` AS game_index,
				games.score AS game_score
			FROM games
			JOIN series ON series.id = games.series_id
			JOIN leagues ON leagues.id = series.league_id
			JOIN bowlers ON bowlers.id = leagues.bowler_id
			WHERE games.id = :gameId 
		""",
	)
	@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
	fun getGameSourceDetails(gameId: UUID): TrackableFilterSourceSummariesEntity

	@RawQuery(observedEntities = [SeriesEntity::class])
	fun getTrackableSeries(query: SimpleSQLiteQuery): PagingSource<Int, TrackableSeriesEntity>
	fun getTrackableSeries(query: String, args: List<Any>): PagingSource<Int, TrackableSeriesEntity> =
		getTrackableSeries(SimpleSQLiteQuery(query, args.toTypedArray()))

	@RawQuery(observedEntities = [GameEntity::class])
	fun getTrackableGames(query: SimpleSQLiteQuery): PagingSource<Int, TrackableGameEntity>
	fun getTrackableGames(query: String, args: List<Any>): PagingSource<Int, TrackableGameEntity> =
		getTrackableGames(SimpleSQLiteQuery(query, args.toTypedArray()))

	@RawQuery(observedEntities = [FrameEntity::class])
	fun getTrackableFrames(query: SimpleSQLiteQuery): PagingSource<Int, TrackableFrameEntity>
	fun getTrackableFrames(query: String, args: List<Any>): PagingSource<Int, TrackableFrameEntity> =
		getTrackableFrames(SimpleSQLiteQuery(query, args.toTypedArray()))
}
