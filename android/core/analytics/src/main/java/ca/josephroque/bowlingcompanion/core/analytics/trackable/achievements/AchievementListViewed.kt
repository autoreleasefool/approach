package ca.josephroque.bowlingcompanion.core.analytics.trackable.achievements

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class AchievementListViewed(
	val title: String,
) : TrackableEvent {
	override val name = "Achievement.ListViewed"
	override val payload: Map<String, String>? = null
}
