package ca.josephroque.bowlingcompanion.core.database

import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
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
}