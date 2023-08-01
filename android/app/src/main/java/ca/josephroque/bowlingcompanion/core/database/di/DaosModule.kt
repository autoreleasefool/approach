package ca.josephroque.bowlingcompanion.core.database.di

import ca.josephroque.bowlingcompanion.core.database.ApproachDatabase
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
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
	fun providesBowlersDao(
		database: ApproachDatabase,
	): BowlerDao = database.bowlerDao()

	@Provides
	fun providesTeamsDao(
		database: ApproachDatabase,
	): TeamDao = database.teamDao()

	@Provides
	fun providesLegacyIDMappingsDao(
		database: ApproachDatabase,
	): LegacyIDMappingDao = database.legacyIDMappingDao()

	@Provides
	fun providesTransactionRunnersDao(
		database: ApproachDatabase,
	): TransactionRunnerDao = database.transactionRunnerDao()

	@Provides
	fun providesTransactionRunner(
		transactionRunnerDao: TransactionRunnerDao,
	): TransactionRunner = transactionRunnerDao
}