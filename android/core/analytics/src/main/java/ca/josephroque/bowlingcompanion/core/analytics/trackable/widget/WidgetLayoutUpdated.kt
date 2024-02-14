package ca.josephroque.bowlingcompanion.core.analytics.trackable.widget

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class WidgetLayoutUpdated(
	val context: String,
	val numberOfWidgets: Int,
) : TrackableEvent {
	override val name = "Widget.LayoutUpdated"
	override val payload = mapOf(
		"Context" to context,
		"NumberOfWidgets" to numberOfWidgets.toString(),
	)
}
