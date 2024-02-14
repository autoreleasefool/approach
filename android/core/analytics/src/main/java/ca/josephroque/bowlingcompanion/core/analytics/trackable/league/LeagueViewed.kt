package ca.josephroque.bowlingcompanion.core.analytics.trackable.league

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object LeagueViewed : TrackableEvent {
	override val name = "League.Viewed"
	override val payload = emptyMap<String, String>()
}
