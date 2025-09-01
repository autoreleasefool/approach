package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.achievements.ConsumableAchievementEvent
import ca.josephroque.bowlingcompanion.core.achievements.EarnableAchievement
import ca.josephroque.bowlingcompanion.core.model.Achievement
import ca.josephroque.bowlingcompanion.core.model.AchievementListItem
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

interface AchievementsRepository {
	fun getEarnedAchievements(): Flow<List<AchievementListItem>>
	fun getLatestAchievement(startDate: Instant): Flow<Achievement?>

	suspend fun insertEvent(event: ConsumableAchievementEvent)
	suspend fun hasEarnedAchievement(achievement: EarnableAchievement): Boolean
}
