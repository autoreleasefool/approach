package ca.josephroque.bowlingcompanion.core.analytics.trackable.gear

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent
import ca.josephroque.bowlingcompanion.core.model.GearKind

data class GearCreated(
	val kind: GearKind,
): TrackableEvent {
	override val name = "Gear.Created"
	override val payload = mapOf(
		"Kind" to kind.name,
	)
}