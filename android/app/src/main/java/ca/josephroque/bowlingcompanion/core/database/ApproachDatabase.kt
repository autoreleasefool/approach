package ca.josephroque.bowlingcompanion.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.database.util.BowlerKindConverter
import ca.josephroque.bowlingcompanion.core.database.util.ExcludeFromStatisticsConverter
import ca.josephroque.bowlingcompanion.core.database.util.InstantConverter
import ca.josephroque.bowlingcompanion.core.database.util.LeagueRecurrenceConverter
import ca.josephroque.bowlingcompanion.core.database.util.SeriesPreBowlConverter
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl

@Database(
	entities = [
		BowlerEntity::class,
		LeagueEntity::class,
		SeriesEntity::class,
	],
	version = 3,
)
@TypeConverters(
	BowlerKindConverter::class,
	ExcludeFromStatisticsConverter::class,
	LeagueRecurrenceConverter::class,
	SeriesPreBowlConverter::class,
	InstantConverter::class,
)
abstract class ApproachDatabase : RoomDatabase() {
	abstract fun bowlerDao(): BowlerDao
	abstract fun leagueDao(): LeagueDao
	abstract fun seriesDao(): SeriesDao
}