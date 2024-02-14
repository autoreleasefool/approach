package ca.josephroque.bowlingcompanion.core.data.queries

import ca.josephroque.bowlingcompanion.core.common.utils.mapOfNullableValues
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter

data class TrackableLeagueQueryComponents(
	val tableAlias: String = "leagues",
	val filter: TrackableFilter.LeagueFilter,
) {
	fun buildFromClause(): String = "FROM leagues AS $tableAlias"

	fun buildWhereClauses(): List<String> {
		val whereConditions = mutableListOf<String>()

		// Filter excluded leagues
		whereConditions.add("$tableAlias.exclude_from_statistics = \"INCLUDE\"")
		whereConditions.add("$tableAlias.archived_on IS NULL")

		if (filter.recurrence != null) {
			whereConditions.add("$tableAlias.recurrence = ?$tableAlias.recurrence")
		}

		return whereConditions
	}

	fun whereClauseArgs(): Map<String, Any> = mapOfNullableValues(
		"$tableAlias.recurrence" to filter.recurrence,
	)

	fun buildOrderClause(): List<String> = listOf("$tableAlias.name ASC")
}
