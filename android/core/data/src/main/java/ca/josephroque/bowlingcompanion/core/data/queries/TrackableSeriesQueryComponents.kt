package ca.josephroque.bowlingcompanion.core.data.queries

import ca.josephroque.bowlingcompanion.core.common.utils.mapOfNullableValues
import ca.josephroque.bowlingcompanion.core.data.queries.TrackableFrameQueryComponents
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter

data class TrackableSeriesQueryComponents(
	override val tableAlias: String = "series",
	val source: TrackableFilter.Source,
	val filter: TrackableFilter.SeriesFilter,
) : QueryComponent {
	constructor(filter: TrackableFilter) : this(source = filter.source, filter = filter.series)

	override fun buildFromClause(): String = "FROM series AS $tableAlias"

	override fun buildJoinClause(parentTable: String, parentColumn: String, childColumn: String): String =
		" JOIN series AS $tableAlias ON $tableAlias.$childColumn = $parentTable.$parentColumn"

	override fun buildWhereClauses(): List<String> {
		val whereConditions = mutableListOf<String>()

		// Filter excluded series
		whereConditions.add("$tableAlias.exclude_from_statistics = 'INCLUDE'")
		whereConditions.add("$tableAlias.archived_on IS NULL")

		if (filter.startDate != null) {
			whereConditions.add("$tableAlias.date >= ?$tableAlias.startDate")
		}

		if (filter.endDate != null) {
			whereConditions.add("$tableAlias.date <= ?$tableAlias.endDate")
		}

		when (filter.alleys) {
			is TrackableFilter.AlleyFilter.Alley ->
				whereConditions.add("$tableAlias.alley_id = ?$tableAlias.alleyId")
			is TrackableFilter.AlleyFilter.Properties -> {
				val properties = filter.alleys as TrackableFilter.AlleyFilter.Properties
				if (properties.material != null) {
					whereConditions.add("$tableAlias.alley_material = ?$tableAlias.alleyMaterial")
				}
				if (properties.mechanism != null) {
					whereConditions.add("$tableAlias.alley_mechanism = ?$tableAlias.alleyMechanism")
				}
				if (properties.pinFall != null) {
					whereConditions.add("$tableAlias.alley_pin_fall = ?$tableAlias.alleyPinFall")
				}
				if (properties.pinBase != null) {
					whereConditions.add("$tableAlias.alley_pin_base = ?$tableAlias.alleyPinBase")
				}
			}
			null -> Unit
		}

		return whereConditions
	}

	override fun whereClauseArgs(): Map<String, Any> = mapOfNullableValues(
		"$tableAlias.startDate" to filter.startDate.toString(),
		"$tableAlias.endDate" to filter.endDate.toString(),
		"$tableAlias.alleyId" to (filter.alleys as? TrackableFilter.AlleyFilter.Alley)?.id,
		"$tableAlias.alleyMaterial" to
			(filter.alleys as? TrackableFilter.AlleyFilter.Properties)?.material,
		"$tableAlias.alleyMechanism" to
			(filter.alleys as? TrackableFilter.AlleyFilter.Properties)?.mechanism,
		"$tableAlias.alleyPinFall" to
			(filter.alleys as? TrackableFilter.AlleyFilter.Properties)?.pinFall,
		"$tableAlias.alleyPinBase" to
			(filter.alleys as? TrackableFilter.AlleyFilter.Properties)?.pinBase,
	)

	override fun buildOrderClause(): List<String> = listOf("$tableAlias.date ASC")
}
