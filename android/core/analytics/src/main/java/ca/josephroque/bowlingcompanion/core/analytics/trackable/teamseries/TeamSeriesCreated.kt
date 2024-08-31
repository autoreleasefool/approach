package ca.josephroque.bowlingcompanion.core.analytics.trackable.teamseries

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object TeamSeriesCreated : TrackableEvent {
	override val name = "TeamSeries.Created"
	override val payload = emptyMap<String, String>()
}
