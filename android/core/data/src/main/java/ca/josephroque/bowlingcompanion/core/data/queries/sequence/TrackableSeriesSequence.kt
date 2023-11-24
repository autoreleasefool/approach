package ca.josephroque.bowlingcompanion.core.data.queries.sequence

import androidx.paging.PagingSource
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableGameQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableLeagueQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableSeriesQueryComponents
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.database.model.TrackableSeriesEntity
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSeriesConfiguration

data class TrackableSeriesSequence(
	val filter: TrackableFilter,
	val statisticsDao: StatisticsDao,
	val configuration: TrackablePerSeriesConfiguration,
): TrackableSequence<TrackableSeriesEntity, TrackableSeries>() {
	private val leaguesQuery = TrackableLeagueQueryComponents(filter = filter.leagues)
	private val seriesQuery = TrackableSeriesQueryComponents(filter = filter.series)

	// Pass default filter because we want all un-excluded games for counting the total games/score
	private val gamesQuery = TrackableGameQueryComponents(filter = TrackableFilter.GameFilter())

	override fun getPagingSource(
		query: String,
		whereArgs: List<Any>
	): PagingSource<Int, TrackableSeriesEntity> = statisticsDao.getTrackableSeries(query, whereArgs)

	override fun adjustByItem(statistics: List<Statistic>, item: TrackableSeries) {
		statistics.forEach { it.adjustBySeries(series = item, configuration = configuration) }
	}

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
		seriesQuery.buildWhereClause(),
		gamesQuery.buildWhereClause(),
	)
		.flatten()
		.joinToString(prefix = "WHERE ", separator = " AND ")

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
	)
		.flatten()
		.joinToString(prefix = "ORDER BY ", separator = ", ")
}