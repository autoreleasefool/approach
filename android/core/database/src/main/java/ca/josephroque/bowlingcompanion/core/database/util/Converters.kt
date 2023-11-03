package ca.josephroque.bowlingcompanion.core.database.util

import androidx.room.TypeConverter
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.model.Pin
import kotlinx.datetime.LocalDate

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