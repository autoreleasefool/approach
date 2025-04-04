package ca.josephroque.bowlingcompanion.core.analytics.trackable.game

import ca.josephroque.bowlingcompanion.core.analytics.GameSessionTrackableEvent
import ca.josephroque.bowlingcompanion.core.model.GameID
import java.util.UUID

data class GameViewed(val gameId: GameID) : GameSessionTrackableEvent {
	override val eventId: UUID
		get() = gameId.value
	override val name = "Game.Viewed"
	override val payload = emptyMap<String, String>()
}
