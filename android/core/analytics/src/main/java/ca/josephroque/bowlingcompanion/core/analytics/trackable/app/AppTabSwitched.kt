package ca.josephroque.bowlingcompanion.core.analytics.trackable.app

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class AppTabSwitched(
	val tabName: String,
) : TrackableEvent {
	override val name = "App.TabSwitched"
	override val payload = mapOf(
		"Tab" to tabName,
	)
}
