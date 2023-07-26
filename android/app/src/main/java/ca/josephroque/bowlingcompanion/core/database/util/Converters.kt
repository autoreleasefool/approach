package ca.josephroque.bowlingcompanion.core.database.util

import androidx.room.TypeConverter
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.asBowlerKind
import ca.josephroque.bowlingcompanion.core.model.asExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.asLeagueRecurrence

class BowlerKindConverter {
	@TypeConverter
	fun bowlerKindToString(value: BowlerKind?): String? =
		value?.let(BowlerKind::name)

	@TypeConverter
	fun stringToBowlerKind(name: String?): BowlerKind? =
		name.asBowlerKind()
}

class LeagueRecurrenceConverter {
	@TypeConverter
	fun leagueRecurrenceToString(value: LeagueRecurrence?): String? =
		value?.let(LeagueRecurrence::name)

	@TypeConverter
	fun stringToLeagueRecurrence(name: String?): LeagueRecurrence? =
		name.asLeagueRecurrence()
}

class ExcludeFromStatisticsConverter {
	@TypeConverter
	fun excludeFromStatisticsToString(value: ExcludeFromStatistics?): String? =
		value?.let(ExcludeFromStatistics::name)

	@TypeConverter
	fun stringToExcludeFromStatistics(name: String?): ExcludeFromStatistics? =
		name.asExcludeFromStatistics()
}