package ca.josephroque.bowlingcompanion.core.analytics.trackable.bowler

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent
import ca.josephroque.bowlingcompanion.core.model.BowlerKind

data class BowlerArchived(
	val kind: BowlerKind,
) : TrackableEvent {
	override val name = "Bowler.Archived"
	override val payload = mapOf(
		"Kind" to kind.name,
	)
}
