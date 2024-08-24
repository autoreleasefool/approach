package ca.josephroque.bowlingcompanion.core.analytics.trackable.team

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object TeamViewed : TrackableEvent {
		override val name = "Team.Viewed"
		override val payload: Map<String, String>? = null
}