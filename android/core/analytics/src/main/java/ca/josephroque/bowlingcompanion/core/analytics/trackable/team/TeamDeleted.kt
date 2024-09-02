package ca.josephroque.bowlingcompanion.core.analytics.trackable.team

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object TeamDeleted : TrackableEvent {
	override val name = "Team.Deleted"
	override val payload: Map<String, String>? = null
}
