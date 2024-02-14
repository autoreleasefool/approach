package ca.josephroque.bowlingcompanion.core.data.queries.sequence

import androidx.paging.PagingSource
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableFrameQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableGameQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableLeagueQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableSeriesQueryComponents
import ca.josephroque.bowlingcompanion.core.data.queries.utils.buildWhereClause
import ca.josephroque.bowlingcompanion.core.data.queries.utils.whereClauseArgs
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.database.model.TrackableFrameEntity
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame

data class TrackableFramesSequence(
	val filter: TrackableFilter,
	val statisticsDao: StatisticsDao,
) : TrackableSequence<TrackableFrameEntity, TrackableFrame>() {
	private val leaguesQuery = TrackableLeagueQueryComponents(filter = filter.leagues)
	private val seriesQuery = TrackableSeriesQueryComponents(filter = filter.series)
	private val gamesQuery = TrackableGameQueryComponents(filter = filter.games)
	private val framesQuery = TrackableFrameQueryComponents(filter = filter.frames)

	override fun getPagingSource(
		query: String,
		whereArgs: List<Any>,
	): PagingSource<Int, TrackableFrameEntity> = statisticsDao.getTrackableFrames(query, whereArgs)

	override fun mapEntityToModel(entity: TrackableFrameEntity) = entity.asModel()

	override fun buildColumnsStatement() = listOf(
		"${seriesQuery.tableAlias}.id AS seriesId",
		"${seriesQuery.tableAlias}.`date` AS `date`",
		"${gamesQuery.tableAlias}.id AS gameId",
		"${gamesQuery.tableAlias}.`index` AS gameIndex",
		"${framesQuery.tableAlias}.`index` AS `index`",
		"${framesQuery.tableAlias}.roll0 AS roll0",
		"${framesQuery.tableAlias}.roll1 AS roll1",
		"${framesQuery.tableAlias}.roll2 AS roll2",
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
		putAll(framesQuery.whereClauseArgs())
		putAll(filter.source.whereClauseArgs())
	}

	override fun buildGroupByStatement() =
		"GROUP BY ${gamesQuery.tableAlias}.id, ${framesQuery.tableAlias}.`index`"

	override fun buildOrderByStatement() = listOf(
		leaguesQuery.buildOrderClause(),
		seriesQuery.buildOrderClause(),
		gamesQuery.buildOrderClause(),
		framesQuery.buildOrderClause(),
	)
		.flatten()
		.joinToString(prefix = "ORDER BY ", separator = ", ")
}
