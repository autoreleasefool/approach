package ca.josephroque.bowlingcompanion.core.analytics.trackable.widget

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class WidgetCreated(
	val context: String,
	val source: String?,
	val statistic: String,
	val timeline: String,
): TrackableEvent {
	override val name = "Widget.Created"
	override val payload = mapOf(
		"Context" to context,
		"Source" to (source ?: ""),
		"Statistic" to statistic,
		"Timeline" to timeline,
	)
}