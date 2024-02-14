package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ViewedAnalytics : TrackableEvent {
	override val name = "Settings.ViewedAnalytics"
	override val payload = emptyMap<String, String>()
}
