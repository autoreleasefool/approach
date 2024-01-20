package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ViewedArchived: TrackableEvent {
	override val name = "Settings.ViewedArchived"
	override val payload = emptyMap<String, String>()
}