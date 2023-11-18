package ca.josephroque.bowlingcompanion.core.data.queries.sequence

import ca.josephroque.bowlingcompanion.core.data.queries.TrackableGameQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableLeagueQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableSeriesQueryComponents
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.database.util.ReadableCursor
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import kotlinx.datetime.LocalDate
import java.util.UUID

data class TrackableGamesSequence(
	val filter: TrackableFilter,
	val statisticsDao: StatisticsDao,
): TrackableSequence<TrackableGame>(statisticsDao) {
	private val leaguesQuery = TrackableLeagueQueryComponents(filter = filter.leagues)
	private val seriesQuery = TrackableSeriesQueryComponents(filter = filter.series)
	private val gamesQuery = TrackableGameQueryComponents(filter = filter.games)

	override fun parseCursor(cursor: ReadableCursor): TrackableGame = TrackableGame(
		seriesId = UUID.fromString(cursor.getString(0)),
		id = UUID.fromString(cursor.getString(1)),
		index = cursor.getInt(2),
		score = cursor.getInt(3),
		date = LocalDate.parse(cursor.getString(4)),
		matchPlay = TrackableGame.MatchPlay(
			id = UUID.fromString(cursor.getString(5)),
			result = cursor.getString(6).let { MatchPlayResult.valueOf(it) },
		),
	)

	override fun buildColumnsStatement(): String = listOf(
		"${gamesQuery.tableAlias}.id AS id",
		"${gamesQuery.tableAlias}.series_id AS seriesId",
		"${gamesQuery.tableAlias}.\"index\" AS \"index\"",
		"${gamesQuery.tableAlias}.score AS score",
		"${seriesQuery.tableAlias}.\"date\" AS date",
		"${gamesQuery.matchPlayTableAlias}.id AS matchPlayId",
		"${gamesQuery.matchPlayTableAlias}.result AS matchPlayResult",
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

	override fun buildGroupByStatement() =
		"GROUP BY ${gamesQuery.tableAlias}.id"

	override fun buildOrderByStatement() = listOf(
		leaguesQuery.buildOrderClause(),
		seriesQuery.buildOrderClause(),
		gamesQuery.buildOrderClause(),
	).joinToString(prefix = "ORDER BY", separator = ", ")
}