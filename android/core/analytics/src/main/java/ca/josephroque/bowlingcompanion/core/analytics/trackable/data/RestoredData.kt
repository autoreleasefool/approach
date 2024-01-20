package ca.josephroque.bowlingcompanion.core.analytics.trackable.data

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object RestoredData: TrackableEvent {
	override val name = "Data.Restored"
	override val payload = emptyMap<String, String>()
}