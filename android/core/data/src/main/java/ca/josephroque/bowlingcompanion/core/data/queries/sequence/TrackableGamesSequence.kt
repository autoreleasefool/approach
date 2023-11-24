package ca.josephroque.bowlingcompanion.core.data.queries.sequence

import androidx.paging.PagingSource
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableGameQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableLeagueQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableSeriesQueryComponents
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.database.model.TrackableGameEntity
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGameConfiguration

data class TrackableGamesSequence(
	val filter: TrackableFilter,
	val statisticsDao: StatisticsDao,
	val configuration: TrackablePerGameConfiguration,
): TrackableSequence<TrackableGameEntity, TrackableGame>() {
	private val leaguesQuery = TrackableLeagueQueryComponents(filter = filter.leagues)
	private val seriesQuery = TrackableSeriesQueryComponents(filter = filter.series)
	private val gamesQuery = TrackableGameQueryComponents(filter = filter.games)

	override fun getPagingSource(
		query: String,
		whereArgs: List<Any>
	): PagingSource<Int, TrackableGameEntity> = statisticsDao.getTrackableGames(query, whereArgs)

	override fun adjustByItem(statistics: List<Statistic>, item: TrackableGame) {
		statistics.forEach { it.adjustByGame(game = item, configuration = configuration) }
	}

	override fun mapEntityToModel(entity: TrackableGameEntity) = entity.asModel()

	override fun buildColumnsStatement(): String = listOf(
		"${gamesQuery.tableAlias}.id AS id",
		"${gamesQuery.tableAlias}.series_id AS seriesId",
		"${gamesQuery.tableAlias}.`index` AS `index`",
		"${gamesQuery.tableAlias}.score AS score",
		"${seriesQuery.tableAlias}.`date` AS date",
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
	)
		.flatten()
		.joinToString(prefix = "WHERE ", separator = " AND ")

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
	)
		.flatten()
		.joinToString(prefix = "ORDER BY ", separator = ", ")
}