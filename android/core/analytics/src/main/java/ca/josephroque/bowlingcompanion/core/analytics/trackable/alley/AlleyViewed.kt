package ca.josephroque.bowlingcompanion.core.analytics.trackable.alley

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object AlleyViewed : TrackableEvent {
	override val name = "Alley.Viewed"
	override val payload: Map<String, String>? = null
}
