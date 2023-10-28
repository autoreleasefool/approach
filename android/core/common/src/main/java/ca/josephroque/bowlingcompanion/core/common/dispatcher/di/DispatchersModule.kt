package ca.josephroque.bowlingcompanion.core.common.dispatcher.di

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
	@Provides
	@Dispatcher(ApproachDispatchers.IO)
	fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

	@Provides
	@Dispatcher(ApproachDispatchers.Default)
	fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}