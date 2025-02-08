package ca.josephroque.bowlingcompanion.core.database.util

import androidx.room.TypeConverter
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.model.Avatar
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class AvatarConverter {
	@TypeConverter
	fun stringToAvatar(value: String?): Avatar? = value?.let(Avatar.Companion::fromString)

	@TypeConverter
	fun avatarToString(avatar: Avatar?): String? = avatar?.toString()
}

class InstantConverter {
	@TypeConverter
	fun longToInstant(value: Long?): Instant? = value?.let(Instant::fromEpochMilliseconds)

	@TypeConverter
	fun instantToLong(instant: Instant?): Long? = instant?.toEpochMilliseconds()
}

class LocalDateConverter {
	@TypeConverter
	fun stringToLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

	@TypeConverter
	fun localDateToString(localDate: LocalDate?): String? = localDate?.toString()
}

class RollConverter {
	@TypeConverter
	fun stringToRoll(value: String?): FrameEntity.Roll? = value?.let { FrameEntity.Roll.fromBitString(it) }

	@TypeConverter
	fun rollToString(roll: FrameEntity.Roll?): String? = roll?.toBitString()
}
