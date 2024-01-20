package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ViewedAcknowledgements: TrackableEvent {
	override val name = "Settings.ViewedAcknowledgements"
	override val payload = emptyMap<String, String>()
}