package ca.josephroque.bowlingcompanion.core.analytics.trackable.app

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object AppLaunched: TrackableEvent {
	override val name = "App.Launched"
	override val payload: Map<String, String>? = null
}