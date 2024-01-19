package ca.josephroque.bowlingcompanion.core.analytics.trackable.league

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object LeagueUpdated: TrackableEvent {
	override val name = "League.Updated"
	override val payload = emptyMap<String, String>()
}