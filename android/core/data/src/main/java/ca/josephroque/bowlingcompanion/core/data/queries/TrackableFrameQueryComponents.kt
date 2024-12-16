package ca.josephroque.bowlingcompanion.core.data.queries

import ca.josephroque.bowlingcompanion.core.common.utils.mapOfNullableValues
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter

data class TrackableFrameQueryComponents(
	override val tableAlias: String = "frames",
	val source: TrackableFilter.Source,
	val filter: TrackableFilter.FrameFilter,
) : QueryComponent {
	constructor(filter: TrackableFilter) : this(source = filter.source, filter = filter.frames)

	override fun buildFromClause(): String = "FROM frames AS $tableAlias"

	override fun buildJoinClause(parentTable: String, parentColumn: String, childColumn: String): String =
		"JOIN frames AS $tableAlias ON $tableAlias.$childColumn = $parentTable.$parentColumn"

	override fun buildWhereClauses(): List<String> {
		return emptyList()
// 		val whereConditions = mutableListOf<String>()

		// FIXME: Filter by bowlingBallsUsed
// 		if (filter.bowlingBallsUsed.isNotEmpty()) {}

// 		return whereConditions
	}

	override fun whereClauseArgs(): Map<String, Any> = mapOfNullableValues()

	override fun buildOrderClause(): List<String> = listOf("$tableAlias.`index` ASC")
}
