package ca.josephroque.bowlingcompanion.core.analytics.trackable.series

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object SeriesCreated: TrackableEvent {
	override val name = "Series.Created"
	override val payload = emptyMap<String, String>()
}