package ca.josephroque.bowlingcompanion.core.analytics.trackable.widget

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class WidgetDeleted(
	val context: String,
) : TrackableEvent {
	override val name = "Widget.Deleted"
	override val payload = mapOf(
		"Context" to context,
	)
}
