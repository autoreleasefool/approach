package ca.josephroque.bowlingcompanion.core.model.stub

import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import java.util.UUID

object BowlerSummaryStub {
	fun list(): List<BowlerSummary> = listOf(
		BowlerSummary(
			id = UUID.randomUUID(),
			name = "Joseph",
		),
		BowlerSummary(
			id = UUID.randomUUID(),
			name = "Sarah",
		),
	)

	fun single(): BowlerSummary = list().first()
}

object BowlerListItemStub {
	fun list(): List<BowlerListItem> = listOf(
		BowlerListItem(
			id = UUID.randomUUID(),
			name = "Joseph",
			average = 200.0
		),
		BowlerListItem(
			id = UUID.randomUUID(),
			name = "Sarah",
			average = 213.5
		),
	)

	fun single(): BowlerListItem = list().first()
}