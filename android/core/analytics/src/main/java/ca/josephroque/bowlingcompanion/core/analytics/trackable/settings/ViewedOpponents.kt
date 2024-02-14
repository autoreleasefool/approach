package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ViewedOpponents : TrackableEvent {
	override val name = "Settings.ViewedOpponents"
	override val payload = emptyMap<String, String>()
}
