package ca.josephroque.bowlingcompanion.core.achievements.earnable

import ca.josephroque.bowlingcompanion.core.achievements.ConsumableAchievementEvent
import ca.josephroque.bowlingcompanion.core.achievements.EarnableAchievement
import ca.josephroque.bowlingcompanion.core.achievements.EarnableAchievementID

object TenYearsAchievement : EarnableAchievement {
	override val id = EarnableAchievementID.TEN_YEARS
	override val events: List<ConsumableAchievementEvent>
		get() = listOf(BadgeClaimed)

	override fun consume(events: List<ConsumableAchievementEvent>): Pair<List<EarnableAchievement>, List<ConsumableAchievementEvent>> {
		val consumed = events.filter { it in this.events }
		return Pair(consumed.map { this }, consumed)
	}

	object BadgeClaimed : ConsumableAchievementEvent {
		override val title = this::class.simpleName ?: "Unknown"
	}
}
