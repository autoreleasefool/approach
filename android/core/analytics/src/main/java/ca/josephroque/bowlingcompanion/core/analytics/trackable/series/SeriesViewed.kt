package ca.josephroque.bowlingcompanion.core.analytics.trackable.series

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object SeriesViewed: TrackableEvent {
	override val name = "Series.Viewed"
	override val payload = emptyMap<String, String>()
}