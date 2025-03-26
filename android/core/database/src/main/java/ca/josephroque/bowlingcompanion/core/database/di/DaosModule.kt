package ca.josephroque.bowlingcompanion.core.database.di

import ca.josephroque.bowlingcompanion.core.database.ApproachDatabase
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
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunnerDao
import ca.josephroque.bowlingcompanion.core.database.legacy.dao.LegacyIDMappingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
	@Provides
	fun providesBowlerDao(database: ApproachDatabase): BowlerDao = database.bowlerDao()

	@Provides
	fun providesTeamDao(database: ApproachDatabase): TeamDao = database.teamDao()

	@Provides
	fun providesTeamBowlerDao(database: ApproachDatabase): TeamBowlerDao = database.teamBowlerDao()

	@Provides
	fun providesLeagueDao(database: ApproachDatabase): LeagueDao = database.leagueDao()

	@Provides
	fun providesSeriesDao(database: ApproachDatabase): SeriesDao = database.seriesDao()

	@Provides
	fun providesTeamSeriesDao(database: ApproachDatabase): TeamSeriesDao = database.teamSeriesDao()

	@Provides
	fun providesGameDao(database: ApproachDatabase): GameDao = database.gameDao()

	@Provides
	fun providesFrameDao(database: ApproachDatabase): FrameDao = database.frameDao()

	@Provides
	fun providesMatchPlayDao(database: ApproachDatabase): MatchPlayDao = database.matchPlayDao()

	@Provides
	fun providesGearDao(database: ApproachDatabase): GearDao = database.gearDao()

	@Provides
	fun providesAlleyDao(database: ApproachDatabase): AlleyDao = database.alleyDao()

	@Provides
	fun providesLaneDao(database: ApproachDatabase): LaneDao = database.laneDao()

	@Provides
	fun providesAchievementDao(database: ApproachDatabase): AchievementDao = database.achievementDao()

	@Provides
	fun providesAchievementEventDao(database: ApproachDatabase): AchievementEventDao = database.achievementEventDao()

	@Provides
	fun providesLegacyIDMappingDao(database: ApproachDatabase): LegacyIDMappingDao = database.legacyIDMappingDao()

	@Provides
	fun providesStatisticsDao(database: ApproachDatabase): StatisticsDao = database.statisticsDao()

	@Provides
	fun providesStatisticsWidgetDao(database: ApproachDatabase): StatisticsWidgetDao = database.statisticsWidgetDao()

	@Provides
	fun providesTransactionRunnerDao(database: ApproachDatabase): TransactionRunnerDao = database.transactionRunnerDao()

	@Provides
	fun providesTransactionRunner(transactionRunnerDao: TransactionRunnerDao): TransactionRunner = transactionRunnerDao

	@Provides
	fun providesCheckpointDao(database: ApproachDatabase): CheckpointDao = database.checkpointDao()
}
