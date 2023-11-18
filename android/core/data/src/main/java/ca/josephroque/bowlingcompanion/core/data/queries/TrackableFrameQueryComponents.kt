package ca.josephroque.bowlingcompanion.core.data.queries

import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter

data class TrackableFrameQueryComponents(
	val tableAlias: String = "frames",
	val filter: TrackableFilter.FrameFilter,
) {
	fun buildJoinClause(parentTable: String, parentColumn: String, childColumn: String): String =
		"JOIN frames AS $tableAlias ON $tableAlias.$childColumn = $parentTable.$parentColumn"

	fun buildWhereClause(): List<String> {
		val whereConditions = mutableListOf<String>()

		if (filter.bowlingBallsUsed.isNotEmpty()) {
			// TODO: Filter by bowlingBallsUsed
		}

		return whereConditions
	}

	fun whereClauseArgs(): Map<String, String> = mapOf()

	fun buildOrderClause(): List<String> = listOf("$tableAlias.`index` ASC")
}