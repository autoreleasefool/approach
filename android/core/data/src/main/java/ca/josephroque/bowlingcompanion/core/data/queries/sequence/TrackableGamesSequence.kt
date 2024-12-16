package ca.josephroque.bowlingcompanion.core.data.queries.sequence

import androidx.paging.PagingSource
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableGameQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableLeagueQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableSeriesQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.utils.buildWhereClause
import ca.josephroque.bowlingcompanion.core.data.queries.utils.whereClauseArgs
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.database.model.TrackableGameEntity
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableGame

data class TrackableGamesSequence(
	val filter: TrackableFilter,
	val statisticsDao: StatisticsDao,
) : TrackableSequence<TrackableGameEntity, TrackableGame>() {
	private val leaguesQuery = TrackableLeagueQueryComponents(filter = filter)
	private val seriesQuery = TrackableSeriesQueryComponents(filter = filter)
	private val gamesQuery = TrackableGameQueryComponents(filter = filter)

	override fun getPagingSource(
		query: String,
		whereArgs: List<Any>,
	): PagingSource<Int, TrackableGameEntity> = statisticsDao.getTrackableGames(query, whereArgs)

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

	override fun buildGroupByStatement() = "GROUP BY ${gamesQuery.tableAlias}.id"

	override fun buildOrderByStatement() = listOf(
		leaguesQuery.buildOrderClause(),
		seriesQuery.buildOrderClause(),
		gamesQuery.buildOrderClause(),
	)
		.flatten()
		.joinToString(prefix = "ORDER BY ", separator = ", ")
}
