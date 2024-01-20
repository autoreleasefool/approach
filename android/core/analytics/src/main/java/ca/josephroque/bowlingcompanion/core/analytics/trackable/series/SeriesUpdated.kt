package ca.josephroque.bowlingcompanion.core.analytics.trackable.series

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object SeriesUpdated: TrackableEvent {
	override val name = "Series.Updated"
	override val payload = emptyMap<String, String>()
}