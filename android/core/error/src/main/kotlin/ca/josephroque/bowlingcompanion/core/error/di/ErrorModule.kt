package ca.josephroque.bowlingcompanion.core.error.di

import ca.josephroque.bowlingcompanion.core.error.ErrorReporting
import ca.josephroque.bowlingcompanion.core.error.SentryErrorReporting
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ErrorModule {
	@Binds
	fun bindsErrorReporting(errorReporting: SentryErrorReporting): ErrorReporting
}
