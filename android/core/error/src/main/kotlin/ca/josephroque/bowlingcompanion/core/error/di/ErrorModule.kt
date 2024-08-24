package ca.josephroque.bowlingcompanion.core.error.di

import ca.josephroque.bowlingcompanion.core.error.ErrorReporting
import ca.josephroque.bowlingcompanion.core.error.SentryErrorReporting
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ErrorModule {
	@Singleton
	@Binds
	fun bindsErrorReporting(errorReporting: SentryErrorReporting): ErrorReporting
}
