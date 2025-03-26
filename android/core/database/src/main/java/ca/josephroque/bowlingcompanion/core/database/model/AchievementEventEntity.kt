package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.AchievementEventID

@Entity(tableName = "achievement_events")
data class AchievementEventEntity(
		@PrimaryKey @ColumnInfo(name = "id", index = true) val achievementId: AchievementEventID,
		@ColumnInfo(name = "title") val title: String,
		@ColumnInfo(name = "is_consumed") val isConsumed: Boolean,
)

data class AchievementEventCreateEntity(
	val id: AchievementEventID,
	val title: String,
	@ColumnInfo(name = "is_consumed") val isConsumed: Boolean,
)