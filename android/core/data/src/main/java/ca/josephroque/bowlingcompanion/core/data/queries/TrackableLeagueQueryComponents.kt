package ca.josephroque.bowlingcompanion.core.data.queries

import ca.josephroque.bowlingcompanion.core.common.utils.mapOfNullableValues
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter

data class TrackableLeagueQueryComponents(
	override val tableAlias: String = "leagues",
	val source: TrackableFilter.Source,
	val filter: TrackableFilter.LeagueFilter,
) : QueryComponent {
	constructor(filter: TrackableFilter) : this(
		source = filter.source,
		filter = when (filter.source) {
			is TrackableFilter.Source.Team,
			is TrackableFilter.Source.Bowler,
			-> filter.leagues
			is TrackableFilter.Source.League,
			is TrackableFilter.Source.Series,
			is TrackableFilter.Source.Game,
			-> TrackableFilter.LeagueFilter()
		},
	)

	override fun buildFromClause(): String = "FROM leagues AS $tableAlias"

	override fun buildJoinClause(
		parentTable: String,
		parentColumn: String,
		childColumn: String,
	): String = "JOIN leagues AS $tableAlias ON $tableAlias.$childColumn = $parentTable.$parentColumn"

	override fun buildWhereClauses(): List<String> {
		when (source) {
			is TrackableFilter.Source.Team,
			is TrackableFilter.Source.Bowler,
			-> Unit
			is TrackableFilter.Source.League,
			is TrackableFilter.Source.Series,
			is TrackableFilter.Source.Game,
			-> return emptyList()
		}

		val whereConditions = mutableListOf<String>()

		// Filter excluded leagues
		whereConditions.add("$tableAlias.exclude_from_statistics = 'INCLUDE'")
		whereConditions.add("$tableAlias.archived_on IS NULL")

		if (filter.recurrence != null) {
			whereConditions.add("$tableAlias.recurrence = ?$tableAlias.recurrence")
		}

		return whereConditions
	}

	override fun whereClauseArgs(): Map<String, Any> = mapOfNullableValues(
		"$tableAlias.recurrence" to filter.recurrence,
	)

	override fun buildOrderClause(): List<String> = listOf("$tableAlias.name ASC")
}
