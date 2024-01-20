package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ViewedDeveloper: TrackableEvent {
	override val name = "Settings.ViewedDeveloper"
	override val payload = emptyMap<String, String>()
}