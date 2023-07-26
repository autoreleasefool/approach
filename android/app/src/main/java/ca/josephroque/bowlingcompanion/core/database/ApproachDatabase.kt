package ca.josephroque.bowlingcompanion.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.database.dao.GearDao
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GearEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.database.util.BowlerKindConverter
import ca.josephroque.bowlingcompanion.core.database.util.ExcludeFromStatisticsConverter
import ca.josephroque.bowlingcompanion.core.database.util.GameLockStateConverter
import ca.josephroque.bowlingcompanion.core.database.util.GameScoringMethodConverter
import ca.josephroque.bowlingcompanion.core.database.util.GearKindConverter
import ca.josephroque.bowlingcompanion.core.database.util.InstantConverter
import ca.josephroque.bowlingcompanion.core.database.util.LeagueRecurrenceConverter
import ca.josephroque.bowlingcompanion.core.database.util.SeriesPreBowlConverter
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl

@Database(
	entities = [
		BowlerEntity::class,
		LeagueEntity::class,
		SeriesEntity::class,
		GameEntity::class,
		GearEntity::class,
	],
	version = 5,
)
@TypeConverters(
	BowlerKindConverter::class,
	ExcludeFromStatisticsConverter::class,
	LeagueRecurrenceConverter::class,
	SeriesPreBowlConverter::class,
	GameLockStateConverter::class,
	GameScoringMethodConverter::class,
	GearKindConverter::class,
	InstantConverter::class,
)
abstract class ApproachDatabase : RoomDatabase() {
	abstract fun bowlerDao(): BowlerDao
	abstract fun leagueDao(): LeagueDao
	abstract fun seriesDao(): SeriesDao
	abstract fun gameDao(): GameDao
	abstract fun gearDao(): GearDao
}