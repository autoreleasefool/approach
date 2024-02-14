package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ViewedSource : TrackableEvent {
	override val name = "Settings.ViewedSource"
	override val payload = emptyMap<String, String>()
}
