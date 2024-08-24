package ca.josephroque.bowlingcompanion.core.featureflags.di

import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.core.featureflags.OverridableFeatureFlagsClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FeatureFlagsModule {
	@Singleton
	@Binds
	fun bindsFeatureFlagsClient(featureFlagsClient: OverridableFeatureFlagsClient): FeatureFlagsClient
}
