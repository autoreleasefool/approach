package ca.josephroque.bowlingcompanion.core.analytics.trackable.league

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object LeagueArchived: TrackableEvent {
	override val name = "League.Archived"
	override val payload = emptyMap<String, String>()
}