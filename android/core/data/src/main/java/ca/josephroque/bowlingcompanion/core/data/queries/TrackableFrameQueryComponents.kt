package ca.josephroque.bowlingcompanion.core.data.queries

import ca.josephroque.bowlingcompanion.core.common.utils.mapOfNullableValues
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter

data class TrackableFrameQueryComponents(
	val tableAlias: String = "frames",
	val filter: TrackableFilter.FrameFilter,
) {
	fun buildJoinClause(parentTable: String, parentColumn: String, childColumn: String): String =
		"JOIN frames AS $tableAlias ON $tableAlias.$childColumn = $parentTable.$parentColumn"

	fun buildWhereClause(): List<String> {
		return emptyList()
// 		val whereConditions = mutableListOf<String>()

		// FIXME: Filter by bowlingBallsUsed
// 		if (filter.bowlingBallsUsed.isNotEmpty()) {}

// 		return whereConditions
	}

	fun whereClauseArgs(): Map<String, Any> = mapOfNullableValues()

	fun buildOrderClause(): List<String> = listOf("$tableAlias.`index` ASC")
}
