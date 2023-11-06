package ca.josephroque.bowlingcompanion.core.database.util

import androidx.room.TypeConverter
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.model.Avatar
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class AvatarConverter {
	@TypeConverter
	fun stringToAvatar(value: String?): Avatar? =
		value?.let {
			val (primary,	secondary) = it.split(";")
			val (pRed, pGreen, pBlue) = primary.split(",")
			val (sRed, sGreen, sBlue) = secondary.split(",")
			Avatar(
				primaryColor = Avatar.RGB(pRed.toInt(), pGreen.toInt(), pBlue.toInt()),
				secondaryColor = Avatar.RGB(sRed.toInt(), sGreen.toInt(), sBlue.toInt()),
			)
		}

	@TypeConverter
	fun avatarRToString(avatar: Avatar?): String? =
		avatar?.let {
			val primary = "${it.primaryColor.red},${it.primaryColor.green},${it.primaryColor.blue}"
			val secondary = "${it.secondaryColor.red},${it.secondaryColor.green},${it.secondaryColor.blue}"
			"$primary;$secondary"
		}
}

class InstantConverter {
	@TypeConverter
	fun longToInstant(value: Long?): Instant? =
		value?.let(Instant::fromEpochMilliseconds)

	@TypeConverter
	fun instantToLong(instant: Instant?): Long? =
		instant?.toEpochMilliseconds()
}

class LocalDateConverter {
	@TypeConverter
	fun stringToLocalDate(value: String?): LocalDate? =
		value?.let { LocalDate.parse(it) }

	@TypeConverter
	fun localDateToString(localDate: LocalDate?): String? =
		localDate?.toString()
}

class RollConverter {
	@TypeConverter
	fun stringToRoll(value: String?): FrameEntity.Roll? =
		value?.let { FrameEntity.Roll.fromBitString(it) }

	@TypeConverter
	fun rollToString(roll: FrameEntity.Roll?): String? =
		roll?.toBitString()
}