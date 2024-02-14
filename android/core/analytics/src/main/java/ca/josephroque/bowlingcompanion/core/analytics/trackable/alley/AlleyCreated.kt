package ca.josephroque.bowlingcompanion.core.analytics.trackable.alley

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class AlleyCreated(
	val withLocation: Boolean,
	val numberOfLanes: Int,
) : TrackableEvent {
	override val name = "Alley.Updated"
	override val payload = mapOf(
		"WithLocation" to withLocation.toString(),
		"NumberOfLanes" to numberOfLanes.toString(),
	)
}
