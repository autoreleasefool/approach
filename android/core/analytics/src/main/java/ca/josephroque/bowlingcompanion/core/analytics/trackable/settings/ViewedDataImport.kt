package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ViewedDataImport: TrackableEvent {
	override val name = "Settings.ViewedDataImport"
	override val payload = emptyMap<String, String>()
}