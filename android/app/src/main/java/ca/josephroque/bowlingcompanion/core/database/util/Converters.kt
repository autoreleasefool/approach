package ca.josephroque.bowlingcompanion.core.database.util

import androidx.room.TypeConverter
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.asBowlerKind
import ca.josephroque.bowlingcompanion.core.model.asExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.asGameLockState
import ca.josephroque.bowlingcompanion.core.model.asGameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.asGearKind
import ca.josephroque.bowlingcompanion.core.model.asLeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.asSeriesPreBowl
import kotlinx.datetime.Instant

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

class SeriesPreBowlConverter {
	@TypeConverter
	fun seriesPreBowlToString(value: SeriesPreBowl?): String? =
		value?.let(SeriesPreBowl::name)

	@TypeConverter
	fun stringToSeriesPreBowl(name: String?): SeriesPreBowl? =
		name.asSeriesPreBowl()
}

class GameLockStateConverter {
	@TypeConverter
	fun gameLockStateToString(value: GameLockState?): String? =
		value?.let(GameLockState::name)

	@TypeConverter
	fun stringToGameLockState(name: String?): GameLockState? =
		name.asGameLockState()
}

class GameScoringMethodConverter {
	@TypeConverter
	fun gameScoringMethodToString(value: GameScoringMethod?): String? =
		value?.let(GameScoringMethod::name)

	@TypeConverter
	fun stringToGameScoringMethod(name: String?): GameScoringMethod? =
		name.asGameScoringMethod()
}

class GearKindConverter {
	@TypeConverter
	fun gearKindToString(value: GearKind?): String? =
		value?.let(GearKind::name)

	@TypeConverter
	fun stringToGearKind(name: String?): GearKind? =
		name.asGearKind()
}

class InstantConverter {
	@TypeConverter
	fun longToInstant(value: Long?): Instant? =
		value?.let(Instant::fromEpochMilliseconds)

	@TypeConverter
	fun instantToLong(instant: Instant?): Long? =
		instant?.toEpochMilliseconds()
}