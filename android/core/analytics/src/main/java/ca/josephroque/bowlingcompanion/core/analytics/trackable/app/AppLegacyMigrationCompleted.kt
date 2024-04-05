package ca.josephroque.bowlingcompanion.core.analytics.trackable.app

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object AppLegacyMigrationCompleted : TrackableEvent {
	override val name = "App.LegacyMigrationCompleted"
	override val payload = emptyMap<String, String>()
}
