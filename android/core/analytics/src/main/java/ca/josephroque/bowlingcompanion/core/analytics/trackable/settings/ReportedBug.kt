package ca.josephroque.bowlingcompanion.core.analytics.trackable.settings

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object ReportedBug : TrackableEvent {
	override val name = "Settings.ReportedBug"
	override val payload = emptyMap<String, String>()
}
