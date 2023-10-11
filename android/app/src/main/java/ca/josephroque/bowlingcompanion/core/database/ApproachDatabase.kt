package ca.josephroque.bowlingcompanion.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.CheckpointDao
import ca.josephroque.bowlingcompanion.core.database.dao.FrameDao
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.dao.MatchPlayDao
import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamBowlerDao
import ca.josephroque.bowlingcompanion.core.database.legacy.dao.LegacyIDMappingDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunnerDao
import ca.josephroque.bowlingcompanion.core.database.model.AlleyEntity
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameGearCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.GameLaneCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.GearEntity
import ca.josephroque.bowlingcompanion.core.database.model.LaneEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingEntity
import ca.josephroque.bowlingcompanion.core.database.model.BowlerPreferredGearCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.LocationEntity
import ca.josephroque.bowlingcompanion.core.database.model.MatchPlayEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesLaneCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.TeamBowlerCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.TeamEntity
import ca.josephroque.bowlingcompanion.core.database.util.LocalDateConverter

@Database(
	entities = [
		BowlerEntity::class,
		LeagueEntity::class,
		SeriesEntity::class,
		GameEntity::class,
		GearEntity::class,
		LocationEntity::class,
		AlleyEntity::class,
		LaneEntity::class,
		FrameEntity::class,
		MatchPlayEntity::class,
		GameGearCrossRef::class,
		GameLaneCrossRef::class,
		BowlerPreferredGearCrossRef::class,
		SeriesLaneCrossRef::class,
		TeamEntity::class,
		TeamBowlerCrossRef::class,
		LegacyIDMappingEntity::class,
	],
	version = 1,
)
@TypeConverters(
	LocalDateConverter::class,
)
abstract class ApproachDatabase : RoomDatabase() {
	abstract fun bowlerDao(): BowlerDao
	abstract fun teamDao(): TeamDao
	abstract fun leagueDao(): LeagueDao
	abstract fun seriesDao(): SeriesDao
	abstract fun gameDao(): GameDao
	abstract fun frameDao(): FrameDao
	abstract fun matchPlayDao(): MatchPlayDao
	abstract fun teamBowlerDao(): TeamBowlerDao

	abstract fun legacyIDMappingDao(): LegacyIDMappingDao

	abstract fun transactionRunnerDao(): TransactionRunnerDao
	abstract fun checkpointDao(): CheckpointDao
}