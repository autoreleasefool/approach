package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import java.util.UUID
import kotlinx.parcelize.Parcelize
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@JvmInline
@Parcelize
value class AchievementID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): AchievementID = AchievementID(UUID.randomUUID())
		fun fromString(string: String): AchievementID = AchievementID(UUID.fromString(string))
	}
}

@OptIn(ExperimentalTime::class)
data class Achievement(
	val id: AchievementID,
	val title: String,
	val earnedAt: Instant,
)

@OptIn(ExperimentalTime::class)
data class AchievementListItem(
	val title: String,
	val firstEarnedAt: Instant,
	val count: Int,
)
