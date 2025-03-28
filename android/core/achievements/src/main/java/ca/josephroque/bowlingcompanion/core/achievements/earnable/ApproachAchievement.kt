package ca.josephroque.bowlingcompanion.core.achievements.earnable

import ca.josephroque.bowlingcompanion.core.achievements.ConsumableAchievementEvent
import ca.josephroque.bowlingcompanion.core.achievements.EarnableAchievement
import ca.josephroque.bowlingcompanion.core.achievements.EarnableAchievementID

object ApproachAchievement : EarnableAchievement {
	override val id = EarnableAchievementID.APPROACH
	override val events: List<ConsumableAchievementEvent>
		get() = listOf(BasicAchievementEarned)

	override fun consume(events: List<ConsumableAchievementEvent>): Pair<List<EarnableAchievement>, List<ConsumableAchievementEvent>> {
		val consumed = events.filter { it in this.events }
		return Pair(consumed.map { this }, consumed)
	}

	object BasicAchievementEarned : ConsumableAchievementEvent {
		override val title = this::class.simpleName ?: "Unknown"
	}
}
