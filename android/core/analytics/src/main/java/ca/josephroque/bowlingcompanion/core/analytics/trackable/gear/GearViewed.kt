package ca.josephroque.bowlingcompanion.core.analytics.trackable.gear

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent
import ca.josephroque.bowlingcompanion.core.model.GearKind

data class GearViewed(
	val kind: GearKind,
): TrackableEvent {
	override val name = "Gear.Viewed"
	override val payload = mapOf(
		"Kind" to kind.name,
	)
}