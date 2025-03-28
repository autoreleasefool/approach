package ca.josephroque.bowlingcompanion.core.achievements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ca.josephroque.bowlingcompanion.core.achievements.earnable.ApproachAchievement
import ca.josephroque.bowlingcompanion.core.achievements.earnable.TenYearsAchievement

interface EarnableAchievement {
	val id: EarnableAchievementID
	val events: List<ConsumableAchievementEvent>

	fun consume(events: List<ConsumableAchievementEvent>): Pair<List<EarnableAchievement>, List<ConsumableAchievementEvent>> {
		val consumed = events.filter { it in this.events }
		return Pair(consumed.map { this }, consumed)
	}

	companion object {
		val all: List<EarnableAchievement> = listOf(
			ApproachAchievement,
			TenYearsAchievement,
		)

		fun fromId(id: EarnableAchievementID): EarnableAchievement? {
			return all.firstOrNull { it.id == id }
		}

		fun fromEvent(event: ConsumableAchievementEvent): EarnableAchievement? {
			return all.firstOrNull { it.events.contains(event) }
		}
	}
}

interface ConsumableAchievementEvent {
	val title: String
}

enum class EarnableAchievementID(
	@StringRes val titleResourceId: Int,
	@DrawableRes val iconResourceId: Int,
	val isEnabled: Boolean,
	val isShownOnEarn: Boolean = true
) {
	APPROACH(
		titleResourceId = R.string.achievement_title_approach,
		iconResourceId = R.drawable.achievement_approach,
		isEnabled = false,
		isShownOnEarn = false,
	),

	TEN_YEARS(
		titleResourceId = R.string.achievement_title_ten_years,
		iconResourceId = R.drawable.achievement_ten_years,
		isEnabled = true,
		isShownOnEarn = false,
	),
	;
}
