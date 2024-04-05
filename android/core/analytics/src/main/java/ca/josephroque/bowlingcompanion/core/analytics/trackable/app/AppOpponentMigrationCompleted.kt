package ca.josephroque.bowlingcompanion.core.analytics.trackable.app

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object AppOpponentMigrationCompleted : TrackableEvent {
	override val name = "App.OpponentMigrationCompleted"
	override val payload = emptyMap<String, String>()
}
