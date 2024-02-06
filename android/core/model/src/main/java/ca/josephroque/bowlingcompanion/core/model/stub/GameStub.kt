package ca.josephroque.bowlingcompanion.core.model.stub

import ca.josephroque.bowlingcompanion.core.model.GameListItem
import java.util.UUID

object GameListItemStub {
	fun list() = listOf(
		GameListItem(
			id = UUID.randomUUID(),
			index = 0,
			score = 300,
		),
		GameListItem(
			id = UUID.randomUUID(),
			index = 1,
			score = 225,
		),
	)

	fun single() = list().first()
}