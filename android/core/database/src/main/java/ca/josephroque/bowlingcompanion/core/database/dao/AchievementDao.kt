package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.AchievementCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.AchievementEntity
import ca.josephroque.bowlingcompanion.core.model.Achievement
import ca.josephroque.bowlingcompanion.core.model.AchievementListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
abstract class AchievementDao {
	@Query(
		"""
			SELECT
				achievements.title AS title,
				min(achievements.earned_at) AS firstEarnedAt,
				count(achievements.id) AS count
			FROM achievements
			GROUP BY achievements.title
		"""
	)
	abstract fun getEarnedAchievements(): Flow<List<AchievementListItem>>

	@Query(
		"""
			SELECT
				achievements.id AS id,
				achievements.title AS title,
				achievements.earned_at AS earnedAt
			FROM achievements
			WHERE achievements.earned_at >= :startDate
			ORDER BY achievements.earned_at DESC
			LIMIT 1
		"""
	)
	abstract fun getLatestAchievement(startDate: Instant): Flow<Achievement>

	@Insert(entity = AchievementEntity::class)
	abstract fun insertAchievement(achievement: AchievementCreateEntity)
}