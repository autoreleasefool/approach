package ca.josephroque.bowlingcompanion.core.analytics.trackable.game

import ca.josephroque.bowlingcompanion.core.analytics.GameSessionTrackableEvent
import java.util.UUID

data class GameViewed(
	override val eventId: UUID,
) : GameSessionTrackableEvent {
	override val name = "Game.ManualScoreSet"
	override val payload = emptyMap<String, String>()
}
