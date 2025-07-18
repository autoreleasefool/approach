package ca.josephroque.bowlingcompanion.core.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.josephroque.bowlingcompanion.core.database.dao.AchievementDao
import ca.josephroque.bowlingcompanion.core.database.dao.AchievementEventDao
import ca.josephroque.bowlingcompanion.core.database.dao.AlleyDao
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.CheckpointDao
import ca.josephroque.bowlingcompanion.core.database.dao.FrameDao
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.database.dao.GearDao
import ca.josephroque.bowlingcompanion.core.database.dao.LaneDao
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.dao.MatchPlayDao
import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsWidgetDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamBowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamSeriesDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunnerDao
import ca.josephroque.bowlingcompanion.core.database.legacy.dao.LegacyIDMappingDao
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingEntity
import ca.josephroque.bowlingcompanion.core.database.migration.MIGRATION_6_7
import ca.josephroque.bowlingcompanion.core.database.model.AchievementEntity
import ca.josephroque.bowlingcompanion.core.database.model.AchievementEventEntity
import ca.josephroque.bowlingcompanion.core.database.model.AlleyEntity
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.BowlerPreferredGearCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameGearCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.GameLaneCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.GearEntity
import ca.josephroque.bowlingcompanion.core.database.model.LaneEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.model.LocationEntity
import ca.josephroque.bowlingcompanion.core.database.model.MatchPlayEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesLaneCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.StatisticsWidgetEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamBowlerCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.TeamEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesSeriesCrossRef
import ca.josephroque.bowlingcompanion.core.database.util.AvatarConverter
import ca.josephroque.bowlingcompanion.core.database.util.InstantConverter
import ca.josephroque.bowlingcompanion.core.database.util.LocalDateConverter
import ca.josephroque.bowlingcompanion.core.database.util.RollConverter

const val DATABASE_NAME = "approach-database"
const val DATABASE_SHM_NAME = "$DATABASE_NAME-shm"
const val DATABASE_WAL_NAME = "$DATABASE_NAME-wal"

@Database(
	version = 7,
	autoMigrations = [
		AutoMigration(from = 1, to = 2),
		AutoMigration(from = 2, to = 3),
		AutoMigration(from = 3, to = 4),
		AutoMigration(from = 4, to = 5),
		AutoMigration(from = 5, to = 6),
	],
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
		StatisticsWidgetEntity::class,
		TeamSeriesEntity::class,
		TeamSeriesSeriesCrossRef::class,
		AchievementEventEntity::class,
		AchievementEntity::class,
	],
)
@TypeConverters(
	AvatarConverter::class,
	LocalDateConverter::class,
	RollConverter::class,
	InstantConverter::class,
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
	abstract fun gearDao(): GearDao
	abstract fun alleyDao(): AlleyDao
	abstract fun laneDao(): LaneDao
	abstract fun teamSeriesDao(): TeamSeriesDao

	abstract fun achievementDao(): AchievementDao
	abstract fun achievementEventDao(): AchievementEventDao

	abstract fun statisticsDao(): StatisticsDao
	abstract fun statisticsWidgetDao(): StatisticsWidgetDao

	abstract fun legacyIDMappingDao(): LegacyIDMappingDao

	abstract fun transactionRunnerDao(): TransactionRunnerDao
	abstract fun checkpointDao(): CheckpointDao

	companion object {
		@Suppress("ktlint:standard:property-naming")
		@Volatile
		private var INSTANCE: ApproachDatabase? = null

		fun getInstance(context: Context): ApproachDatabase {
			synchronized(this) {
				var instance = INSTANCE

				if (instance == null) {
					instance = Room.databaseBuilder(
						context,
						ApproachDatabase::class.java,
						DATABASE_NAME,
					)
						.addMigrations(MIGRATION_6_7)
						.build()

					INSTANCE = instance
				}

				return instance
			}
		}

		fun close() {
			synchronized(this) {
				INSTANCE?.close()
				INSTANCE = null
			}
		}
	}
}
