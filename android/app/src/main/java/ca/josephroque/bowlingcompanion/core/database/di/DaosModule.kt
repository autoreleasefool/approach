package ca.josephroque.bowlingcompanion.core.database.di

import ca.josephroque.bowlingcompanion.core.database.ApproachDatabase
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
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
	fun providesLeaguesDao(
		database: ApproachDatabase,
	): LeagueDao = database.leagueDao()
}