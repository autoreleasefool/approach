package ca.josephroque.bowlingcompanion.core.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import ca.josephroque.bowlingcompanion.core.database.model.TrackableFilterSourceSummariesEntity
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
		"""
	)
	fun getBowlerSourceDetails(bowlerId: UUID): TrackableFilterSourceSummariesEntity

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
		"""
	)
	fun getLeagueSourceDetails(leagueId: UUID): TrackableFilterSourceSummariesEntity

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
		"""
	)
	fun getSeriesSourceDetails(seriesId: UUID): TrackableFilterSourceSummariesEntity

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
		"""
	)
	fun getGameSourceDetails(gameId: UUID): TrackableFilterSourceSummariesEntity

	@RawQuery
	fun getTrackableStatistics(query: SimpleSQLiteQuery): Cursor
	fun getTrackableStatistics(query: String, args: List<Any>): Cursor =
		getTrackableStatistics(SimpleSQLiteQuery(query, args.toTypedArray()))
}