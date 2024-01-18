package ca.josephroque.bowlingcompanion.core.common.dispatcher.di

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.Default
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
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
	@Dispatcher(IO)
	fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

	@Provides
	@Dispatcher(Default)
	fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}