package ca.josephroque.bowlingcompanion.core.analytics.trackable.data

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ExportedData: TrackableEvent {
	override val name = "Data.Exported"
	override val payload = emptyMap<String, String>()
}