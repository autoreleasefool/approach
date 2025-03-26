package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import java.util.UUID

@JvmInline
@Parcelize
value class AchievementID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): AchievementID = AchievementID(UUID.randomUUID())
		fun fromString(string: String): AchievementID = AchievementID(UUID.fromString(string))
	}
}

data class Achievement(
	val id: AchievementID,
	val title: String,
	val earnedAt: Instant,
)

data class AchievementListItem(
	val title: String,
	val firstEarnedAt: Instant,
	val count: Int,
)
