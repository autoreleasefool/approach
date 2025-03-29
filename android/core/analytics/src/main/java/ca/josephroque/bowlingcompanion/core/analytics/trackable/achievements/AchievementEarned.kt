package ca.josephroque.bowlingcompanion.core.analytics.trackable.achievements

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class AchievementEarned(
	val title: String,
) : TrackableEvent {
	override val name = "Achievement.Earned"
	override val payload = mapOf(
		"Achievement" to title,
	)
}
