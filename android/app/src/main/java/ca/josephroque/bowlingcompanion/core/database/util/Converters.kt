package ca.josephroque.bowlingcompanion.core.database.util

import androidx.room.TypeConverter
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.asBowlerKind

class BowlerKindConverter {
	@TypeConverter
	fun bowlerKindToString(value: BowlerKind?): String? =
		value?.let(BowlerKind::name)

	@TypeConverter
	fun stringToBowlerKind(name: String?): BowlerKind? =
		name.asBowlerKind()
}