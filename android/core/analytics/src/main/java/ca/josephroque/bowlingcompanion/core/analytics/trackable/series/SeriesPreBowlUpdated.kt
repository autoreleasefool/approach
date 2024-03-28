package ca.josephroque.bowlingcompanion.core.analytics.trackable.series

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object SeriesPreBowlUpdated : TrackableEvent {
	override val name = "Series.PreBowlUpdated"
	override val payload = emptyMap<String, String>()
}
