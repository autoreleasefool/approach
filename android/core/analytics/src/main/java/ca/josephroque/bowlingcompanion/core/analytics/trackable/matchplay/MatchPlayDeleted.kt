package ca.josephroque.bowlingcompanion.core.analytics.trackable.matchplay

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

@Suppress("unused")
data object MatchPlayDeleted : TrackableEvent {
	override val name = "MatchPlay.Deleted"
	override val payload = emptyMap<String, String>()
}
