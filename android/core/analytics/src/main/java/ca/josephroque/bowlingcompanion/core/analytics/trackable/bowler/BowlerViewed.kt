package ca.josephroque.bowlingcompanion.core.analytics.trackable.bowler

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent
import ca.josephroque.bowlingcompanion.core.model.BowlerKind

data class BowlerViewed(
	val kind: BowlerKind,
) : TrackableEvent {
	override val name = "Bowler.Viewed"
	override val payload = mapOf(
		"Kind" to kind.name,
	)
}
