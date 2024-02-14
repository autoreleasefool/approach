package ca.josephroque.bowlingcompanion.core.analytics.trackable.data

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ImportedData : TrackableEvent {
	override val name = "Data.Imported"
	override val payload = emptyMap<String, String>()
}
