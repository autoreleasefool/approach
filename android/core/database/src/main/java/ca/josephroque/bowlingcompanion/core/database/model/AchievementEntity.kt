package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.AchievementID
import kotlinx.datetime.Instant

@Entity(tableName = "achievements")
data class AchievementEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: AchievementID,
	@ColumnInfo(name = "title") val title: String,
	@ColumnInfo(name = "earned_at") val earnedAt: Instant,
)

data class AchievementCreateEntity(
	val id: AchievementID,
	val title: String,
	@ColumnInfo(name = "earned_at") val earnedAt: Instant,
)
