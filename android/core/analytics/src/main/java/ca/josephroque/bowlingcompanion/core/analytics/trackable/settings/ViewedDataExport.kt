package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ViewedDataExport : TrackableEvent {
	override val name = "Settings.ViewedDataExport"
	override val payload = emptyMap<String, String>()
}
