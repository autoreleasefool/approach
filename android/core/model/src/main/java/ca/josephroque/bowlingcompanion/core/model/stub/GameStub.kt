package ca.josephroque.bowlingcompanion.core.model.stub

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameListItem

object GameStub {
	fun list() = listOf(
		GameListItem(
			id = GameID.randomID(),
			index = 0,
			score = 300,
		),
		GameListItem(
			id = GameID.randomID(),
			index = 1,
			score = 225,
		),
	)
}
