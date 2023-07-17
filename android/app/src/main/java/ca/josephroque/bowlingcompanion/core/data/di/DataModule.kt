package ca.josephroque.bowlingcompanion.core.data.di

import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstBowlersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
	@Binds
	fun bindsBowlerRepository(
		bowlersRepository: OfflineFirstBowlersRepository,
	): BowlersRepository
}