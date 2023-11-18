package ca.josephroque.bowlingcompanion.core.data.queries.sequence

import ca.josephroque.bowlingcompanion.core.data.queries.TrackableGameQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableLeagueQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableSeriesQueryComponents
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.database.util.ReadableCursor
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import kotlinx.datetime.LocalDate
import java.util.UUID

data class TrackableSeriesSequence(
	val filter: TrackableFilter,
	val statisticsDao: StatisticsDao,
): TrackableSequence<TrackableSeries>(statisticsDao) {
	private val leaguesQuery = TrackableLeagueQueryComponents(filter = filter.leagues)
	private val seriesQuery = TrackableSeriesQueryComponents(filter = filter.series)

	// Pass default filter because we want all un-excluded games for counting the total games/score
	private val gamesQuery = TrackableGameQueryComponents(filter = TrackableFilter.GameFilter())

	override fun parseCursor(cursor: ReadableCursor) = TrackableSeries(
		id = UUID.fromString(cursor.getString(0)),
		date = LocalDate.parse(cursor.getString(1)),
		numberOfGames = cursor.getInt(2),
		total = cursor.getInt(3),
	)

	override fun buildColumnsStatement() = listOf(
		"${seriesQuery.tableAlias}.id AS id",
		"${seriesQuery.tableAlias}.\"date\" AS date",
		"COUNT(${gamesQuery.tableAlias}.id) AS numberOfGames",
		"SUM(${gamesQuery.tableAlias}.score) AS total",
	).joinToString(prefix = "SELECT ", separator = ", ")

	override fun buildTablesStatement() = listOf(
		leaguesQuery.buildFromClause(),
		seriesQuery.buildJoinClause(leaguesQuery.tableAlias, "id", "league_id"),
		gamesQuery.buildJoinClause(seriesQuery.tableAlias, "id", "series_id"),
	).joinToString(separator = "\n")

	override fun buildWhereStatement() = listOf(
		leaguesQuery.buildWhereClauses(),
		seriesQuery.buildWhereClause(),
		gamesQuery.buildWhereClause(),
	).joinToString(prefix = "WHERE ", separator = " AND ")

	override fun buildWhereArgs() = mutableMapOf<String, String>().apply {
		putAll(leaguesQuery.whereClauseArgs())
		putAll(seriesQuery.whereClauseArgs())
		putAll(gamesQuery.whereClauseArgs())
	}

	override fun buildGroupByStatement(): String =
		"GROUP BY ${seriesQuery.tableAlias}.id"

	override fun buildOrderByStatement() = listOf(
		leaguesQuery.buildOrderClause(),
		seriesQuery.buildOrderClause(),
		gamesQuery.buildOrderClause(),
	).joinToString(prefix = "ORDER BY ", separator = ", ")
}