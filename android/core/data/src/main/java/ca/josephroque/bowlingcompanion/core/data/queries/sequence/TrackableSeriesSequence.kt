package ca.josephroque.bowlingcompanion.core.data.queries.sequence

import androidx.paging.PagingSource
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableGameQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableLeagueQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableSeriesQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.utils.buildWhereClause
import ca.josephroque.bowlingcompanion.core.data.queries.utils.whereClauseArgs
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.database.model.TrackableSeriesEntity
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries

data class TrackableSeriesSequence(
	val filter: TrackableFilter,
	val statisticsDao: StatisticsDao,
) : TrackableSequence<TrackableSeriesEntity, TrackableSeries>() {
	private val leaguesQuery = TrackableLeagueQueryComponents(filter = filter)
	private val seriesQuery = TrackableSeriesQueryComponents(filter = filter)

	// Pass default filter because we want all un-excluded games for counting the total games/score
	private val gamesQuery = TrackableGameQueryComponents(source = filter.source, filter = TrackableFilter.GameFilter())

	override fun getPagingSource(
		query: String,
		whereArgs: List<Any>,
	): PagingSource<Int, TrackableSeriesEntity> = statisticsDao.getTrackableSeries(query, whereArgs)

	override fun mapEntityToModel(entity: TrackableSeriesEntity) = entity.asModel()

	override fun buildColumnsStatement() = listOf(
		"${seriesQuery.tableAlias}.id AS id",
		"${seriesQuery.tableAlias}.`date` AS `date`",
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
		seriesQuery.buildWhereClauses(),
		gamesQuery.buildWhereClauses(),
		filter.source.buildWhereClause(
			leagueTableAlias = leaguesQuery.tableAlias,
			seriesTableAlias = seriesQuery.tableAlias,
			gameTableAlias = gamesQuery.tableAlias,
		),
	)
		.flatten()
		.joinToString(prefix = "WHERE ", separator = " AND ")

	override fun buildWhereArgs() = mutableMapOf<String, Any>().apply {
		putAll(leaguesQuery.whereClauseArgs())
		putAll(seriesQuery.whereClauseArgs())
		putAll(gamesQuery.whereClauseArgs())
		putAll(filter.source.whereClauseArgs())
	}

	override fun buildGroupByStatement(): String = "GROUP BY ${seriesQuery.tableAlias}.id"

	override fun buildOrderByStatement() = listOf(
		leaguesQuery.buildOrderClause(),
		seriesQuery.buildOrderClause(),
		gamesQuery.buildOrderClause(),
	)
		.flatten()
		.joinToString(prefix = "ORDER BY ", separator = ", ")
}
