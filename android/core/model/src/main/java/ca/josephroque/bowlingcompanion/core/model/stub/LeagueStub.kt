package ca.josephroque.bowlingcompanion.core.model.stub

import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary

object LeagueSummaryStub {
	fun list(): List<LeagueSummary> = listOf(
		LeagueSummary(
			id = LeagueID.randomID(),
			name = "Majors, 2023-2024",
		),
		LeagueSummary(
			id = LeagueID.randomID(),
			name = "Minors, 2024",
		),
	)

	fun single(): LeagueSummary = list().first()
}

@Suppress("unused")
object LeagueListItemStub {
	fun list(): List<LeagueListItem> = listOf(
		LeagueListItem(
			id = LeagueID.randomID(),
			name = "Majors, 2023-2024",
			average = 190.1,
			lastSeriesDate = null,
			recurrence = LeagueRecurrence.REPEATING,
		),
		LeagueListItem(
			id = LeagueID.randomID(),
			name = "Minors, 2024",
			average = 245.4,
			lastSeriesDate = null,
			recurrence = LeagueRecurrence.REPEATING,
		),
	)
}
