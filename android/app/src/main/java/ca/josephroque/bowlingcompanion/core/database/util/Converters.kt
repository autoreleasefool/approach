package ca.josephroque.bowlingcompanion.core.database.util

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class LocalDateConverter {
	@TypeConverter
	fun stringToLocalDate(value: String?): LocalDate? =
		value?.let { LocalDate.parse(it) }

	@TypeConverter
	fun localDateToString(localDate: LocalDate?): String? =
		localDate?.toString()
}