package ca.josephroque.bowlingcompanion.core.analytics.trackable.alley

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object AlleyDeleted: TrackableEvent {
	override val name = "Alley.Deleted"
	override val payload: Map<String, String>? = null
}