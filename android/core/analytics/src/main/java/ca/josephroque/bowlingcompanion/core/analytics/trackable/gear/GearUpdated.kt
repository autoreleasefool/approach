package ca.josephroque.bowlingcompanion.core.analytics.trackable.gear

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent
import ca.josephroque.bowlingcompanion.core.model.GearKind

data class GearUpdated(
	val kind: GearKind,
) : TrackableEvent {
	override val name = "Gear.Updated"
	override val payload = mapOf(
		"Kind" to kind.name,
	)
}
