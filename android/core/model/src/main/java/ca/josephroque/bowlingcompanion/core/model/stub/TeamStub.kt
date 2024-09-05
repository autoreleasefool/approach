package ca.josephroque.bowlingcompanion.core.model.stub

import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSummary

object TeamStub {
	fun list(): List<TeamSummary> = listOf(
		TeamSummary(
			id = TeamID.randomID(),
			name = "Team 1",
		),
		TeamSummary(
			id = TeamID.randomID(),
			name = "Team 2",
		),
	)

	fun single(): TeamSummary = list().first()
}