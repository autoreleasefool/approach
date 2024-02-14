package ca.josephroque.bowlingcompanion.core.model.stub

import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import java.util.UUID

object LeagueSummaryStub {
	fun list(): List<LeagueSummary> = listOf(
		LeagueSummary(
			id = UUID.randomUUID(),
			name = "Majors, 2023-2024",
		),
		LeagueSummary(
			id = UUID.randomUUID(),
			name = "Minors, 2024",
		),
	)

	fun single(): LeagueSummary = list().first()
}

object LeagueListItemStub {
	fun list(): List<LeagueListItem> = listOf(
		LeagueListItem(
			id = UUID.randomUUID(),
			name = "Majors, 2023-2024",
			average = 190.1,
			lastSeriesDate = null,
			recurrence = LeagueRecurrence.REPEATING,
		),
		LeagueListItem(
			id = UUID.randomUUID(),
			name = "Minors, 2024",
			average = 245.4,
			lastSeriesDate = null,
			recurrence = LeagueRecurrence.REPEATING,
		),
	)

	fun single(): LeagueListItem = list().first()
}
