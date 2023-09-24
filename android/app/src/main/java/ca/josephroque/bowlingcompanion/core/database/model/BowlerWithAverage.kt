package ca.josephroque.bowlingcompanion.core.database.model

import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import java.util.UUID

data class BowlerWithAverage(
	val id: UUID,
	val name: String,
	val average: Double?,
)

fun BowlerWithAverage.asListItem() =
	BowlerListItem(id = id, name = name, average = average)