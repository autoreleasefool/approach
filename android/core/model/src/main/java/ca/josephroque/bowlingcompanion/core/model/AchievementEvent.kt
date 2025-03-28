package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import java.util.UUID
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class AchievementEventID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): AchievementEventID = AchievementEventID(UUID.randomUUID())
		fun fromString(string: String): AchievementEventID = AchievementEventID(UUID.fromString(string))
	}
}

data class AchievementEvent(
	val id: AchievementEventID,
	val title: String,
	val isConsumed: Boolean,
)

data class AchievementEventCreate(
	val id: AchievementEventID,
	val title: String,
	val isConsumed: Boolean,
)
