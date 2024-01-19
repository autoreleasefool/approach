package ca.josephroque.bowlingcompanion.core.analytics.trackable.league

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object LeagueCreated: TrackableEvent {
	override val name = "League.Created"
	override val payload = emptyMap<String, String>()
}