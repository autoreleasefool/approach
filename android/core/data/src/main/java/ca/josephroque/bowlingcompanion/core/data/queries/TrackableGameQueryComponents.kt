package ca.josephroque.bowlingcompanion.core.data.queries

import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter

data class TrackableGameQueryComponents(
	val tableAlias: String = "games",
	val filter: TrackableFilter.GameFilter,
) {
	val matchPlayTableAlias = "${tableAlias}MatchPlays"

	fun buildJoinClause(parentTable: String, parentColumn: String, childColumn: String): String = listOf(
		"JOIN games AS $tableAlias ON $tableAlias.$childColumn = $parentTable.$parentColumn",
		"LEFT JOIN match_plays AS $matchPlayTableAlias ON $matchPlayTableAlias.game_id = $tableAlias.id",
	).joinToString("\n")

	fun buildWhereClause(): List<String> {
		val whereConditions = mutableListOf<String>()

		// Filter excluded games
		whereConditions.add("$tableAlias.exclude_from_statistics = \"INCLUDE\"")
		whereConditions.add("$tableAlias.archived_on IS NULL")
		whereConditions.add("$tableAlias.score > 0")

		if (filter.opponent != null) {
			// TODO: Filter by opponent in MatchPlay
		}

		if (filter.gearUsed.isNotEmpty()) {
			// TODO: Filter by gearUsed
		}

		when (filter.lanes) {
			is TrackableFilter.LaneFilter.Lanes -> {
				// TODO: Filter by lanes
			}
			is TrackableFilter.LaneFilter.Positions -> {
				// TODO: Filter by lane positions
			}
			null -> Unit
		}

		return whereConditions
	}

	fun whereClauseArgs(): Map<String, String> = mapOf()

	fun buildOrderClause(): List<String> = listOf("$tableAlias.`index` ASC")
}