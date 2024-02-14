package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object SentFeedback : TrackableEvent {
	override val name = "Settings.SentFeedback"
	override val payload = emptyMap<String, String>()
}
