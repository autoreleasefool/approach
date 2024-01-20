package ca.josephroque.bowlingcompanion.core.analytics.trackable.matchplay

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object MatchPlayCreated: TrackableEvent {
	override val name = "MatchPlay.Created"
	override val payload = emptyMap<String, String>()
}