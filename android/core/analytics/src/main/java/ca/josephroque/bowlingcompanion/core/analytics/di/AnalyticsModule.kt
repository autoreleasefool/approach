package ca.josephroque.bowlingcompanion.core.analytics.di

import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.TelemetryDeckAnalyticsClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AnalyticsModule {
	@Binds
	fun bindsAnalyticsClient(analyticsClient: TelemetryDeckAnalyticsClient): AnalyticsClient
}
