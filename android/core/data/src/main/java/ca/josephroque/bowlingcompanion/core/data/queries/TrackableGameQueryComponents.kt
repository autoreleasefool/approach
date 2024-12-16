package ca.josephroque.bowlingcompanion.core.data.queries

import ca.josephroque.bowlingcompanion.core.common.utils.mapOfNullableValues
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableFrameQueryComponents
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter

data class TrackableGameQueryComponents(
	override val tableAlias: String = "games",
	val source: TrackableFilter.Source,
	val filter: TrackableFilter.GameFilter,
) : QueryComponent {
	val matchPlayTableAlias = "${tableAlias}MatchPlays"

	constructor(filter: TrackableFilter) : this(source = filter.source, filter = filter.games)

	override fun buildFromClause(): String = "FROM games AS $tableAlias"

	override fun buildJoinClause(parentTable: String, parentColumn: String, childColumn: String): String =
		listOf(
			"JOIN games AS $tableAlias ON $tableAlias.$childColumn = $parentTable.$parentColumn",
			"LEFT JOIN match_plays AS $matchPlayTableAlias ON $matchPlayTableAlias.game_id = $tableAlias.id",
		).joinToString("\n")

	override fun buildWhereClauses(): List<String> {
		val whereConditions = mutableListOf<String>()

		// Filter excluded games
		whereConditions.add("$tableAlias.exclude_from_statistics = 'INCLUDE'")
		whereConditions.add("$tableAlias.archived_on IS NULL")
		whereConditions.add("$tableAlias.score > 0")

		// FIXME: Filter by opponent in MatchPlay
// 		if (filter.opponent != null) {}

		// FIXME: Filter by gearUsed
// 		if (filter.gearUsed.isNotEmpty()) { }

		when (filter.lanes) {
			is TrackableFilter.LaneFilter.Lanes -> {
				// FIXME: Filter by lanes
			}
			is TrackableFilter.LaneFilter.Positions -> {
				// FIXME: Filter by lane positions
			}
			null -> Unit
		}

		return whereConditions
	}

	override fun whereClauseArgs(): Map<String, Any> = mapOfNullableValues()

	override fun buildOrderClause(): List<String> = listOf("$tableAlias.`index` ASC")
}
