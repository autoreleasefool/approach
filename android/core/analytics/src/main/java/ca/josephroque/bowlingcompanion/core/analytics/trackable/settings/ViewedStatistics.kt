package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ViewedStatistics : TrackableEvent {
	override val name = "Settings.ViewedStatistics"
	override val payload = emptyMap<String, String>()
}
