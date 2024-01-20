package ca.josephroque.bowlingcompanion.core.analytics.trackable.series

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object SeriesArchived: TrackableEvent {
	override val name = "Series.Archived"
	override val payload = emptyMap<String, String>()
}