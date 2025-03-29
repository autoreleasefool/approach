package ca.josephroque.bowlingcompanion.core.analytics.trackable.achievements

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object AchievementListViewed : TrackableEvent {
	override val name = "Achievement.ListViewed"
	override val payload: Map<String, String>? = null
}
