package ca.josephroque.bowlingcompanion.core.model.stub

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary

object BowlerSummaryStub {
	fun list(): List<BowlerSummary> = listOf(
		BowlerSummary(
			id = BowlerID.randomID(),
			name = "Joseph",
		),
		BowlerSummary(
			id = BowlerID.randomID(),
			name = "Sarah",
		),
	)

	fun single(): BowlerSummary = list().first()
}

@Suppress("unused")
object BowlerListItemStub {
	fun list(): List<BowlerListItem> = listOf(
		BowlerListItem(
			id = BowlerID.randomID(),
			name = "Joseph",
			average = 200.0,
		),
		BowlerListItem(
			id = BowlerID.randomID(),
			name = "Sarah",
			average = 213.5,
		),
	)
}
