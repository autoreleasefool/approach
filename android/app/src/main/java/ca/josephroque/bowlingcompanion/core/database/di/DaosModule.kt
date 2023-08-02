package ca.josephroque.bowlingcompanion.core.database.di

import ca.josephroque.bowlingcompanion.core.database.ApproachDatabase
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.CheckpointDao
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.database.legacy.dao.LegacyIDMappingDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunnerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
	@Provides
	fun providesBowlerDao(
		database: ApproachDatabase,
	): BowlerDao = database.bowlerDao()

	@Provides
	fun providesTeamDao(
		database: ApproachDatabase,
	): TeamDao = database.teamDao()

	@Provides
	fun providesLeagueDao(
		database: ApproachDatabase,
	): LeagueDao = database.leagueDao()

	@Provides
	fun providesSeriesDao(
		database: ApproachDatabase,
	): SeriesDao = database.seriesDao()

	@Provides
	fun providesLegacyIDMappingDao(
		database: ApproachDatabase,
	): LegacyIDMappingDao = database.legacyIDMappingDao()

	@Provides
	fun providesTransactionRunnerDao(
		database: ApproachDatabase,
	): TransactionRunnerDao = database.transactionRunnerDao()

	@Provides
	fun providesTransactionRunner(
		transactionRunnerDao: TransactionRunnerDao,
	): TransactionRunner = transactionRunnerDao

	@Provides
	fun providesCheckpointDao(
		database: ApproachDatabase,
	): CheckpointDao = database.checkpointDao()
}