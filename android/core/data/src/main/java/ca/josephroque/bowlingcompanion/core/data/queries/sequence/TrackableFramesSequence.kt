package ca.josephroque.bowlingcompanion.core.data.queries.sequence

import ca.josephroque.bowlingcompanion.core.data.queries.TrackableFrameQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableGameQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableLeagueQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableSeriesQueryComponents
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.database.util.ReadableCursor
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import kotlinx.datetime.LocalDate
import java.util.UUID

data class TrackableFramesSequence(
	val filter: TrackableFilter,
	val statisticsDao: StatisticsDao,
): TrackableSequence<TrackableFrame>(statisticsDao) {
	private val leaguesQuery = TrackableLeagueQueryComponents(filter = filter.leagues)
	private val seriesQuery = TrackableSeriesQueryComponents(filter = filter.series)
	private val gamesQuery = TrackableGameQueryComponents(filter = filter.games)
	private val framesQuery = TrackableFrameQueryComponents(filter = filter.frames)

	override fun parseCursor(cursor: ReadableCursor) = TrackableFrame(
		seriesId = UUID.fromString(cursor.getString(0)),
		date = LocalDate.parse(cursor.getString(1)),
		gameId = UUID.fromString(cursor.getString(2)),
		gameIndex = cursor.getInt(3),
		index = cursor.getInt(4),
		rolls = emptyList(), // TODO: Figure out how to join multiple cursor rows
	)

	override fun buildColumnsStatement() = listOf(
		"${seriesQuery.tableAlias}.id AS seriesId",
		"${seriesQuery.tableAlias}.\"date\" AS date",
		"${gamesQuery.tableAlias}.id AS gameId",
		"${gamesQuery.tableAlias}.\"index\" AS gameIndex",
		"${framesQuery.tableAlias}.\"index\" AS index",
	).joinToString(prefix = "SELECT ", separator = ", ")

	override fun buildTablesStatement() = listOf(
		leaguesQuery.buildFromClause(),
		seriesQuery.buildJoinClause(leaguesQuery.tableAlias, "id", "league_id"),
		gamesQuery.buildJoinClause(seriesQuery.tableAlias, "id", "series_id"),
		framesQuery.buildJoinClause(gamesQuery.tableAlias, "id", "game_id"),
	).joinToString(separator = "\n")

	override fun buildWhereStatement() = listOf(
		leaguesQuery.buildWhereClauses(),
		seriesQuery.buildWhereClause(),
		gamesQuery.buildWhereClause(),
		framesQuery.buildWhereClause(),
	).joinToString(prefix = "WHERE ", separator = " AND ")

	override fun buildWhereArgs() = mutableMapOf<String, String>().apply {
		putAll(leaguesQuery.whereClauseArgs())
		putAll(seriesQuery.whereClauseArgs())
		putAll(gamesQuery.whereClauseArgs())
		putAll(framesQuery.whereClauseArgs())
	}

	override fun buildGroupByStatement() =
		"GROUP BY ${framesQuery.tableAlias}.id"

	override fun buildOrderByStatement() = listOf(
		leaguesQuery.buildOrderClause(),
		seriesQuery.buildOrderClause(),
		gamesQuery.buildOrderClause(),
		framesQuery.buildOrderClause(),
	).joinToString(prefix = "ORDER BY ", separator = ", ")
}