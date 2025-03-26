package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ca.josephroque.bowlingcompanion.core.database.model.AchievementEventCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.AchievementEventEntity
import ca.josephroque.bowlingcompanion.core.model.AchievementEvent
import ca.josephroque.bowlingcompanion.core.model.AchievementEventID

@Dao
abstract class AchievementEventDao {
	@Query(
		"""
			SELECT
				events.id AS id,
				events.title AS title,
				events.is_consumed AS isConsumed
			FROM achievement_events AS events
			WHERE
				events.is_consumed = 0
				AND events.title IN (:titles)
		"""
	)
	abstract fun getUnconsumedEvents(titles: List<String>): List<AchievementEvent>

	@Query(
		"""
			UPDATE achievement_events
			SET is_consumed = 1
			WHERE id IN (:ids)
		"""
	)
	abstract fun consumeEvents(ids: List<AchievementEventID>)

	@Insert(entity = AchievementEventEntity::class)
	abstract fun insertEvent(event: AchievementEventCreateEntity)
}