package ca.josephroque.bowlingcompanion.core.featureFlags.di

import ca.josephroque.bowlingcompanion.core.featureFlags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.core.featureFlags.OverridableFeatureFlagsClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FeatureFlagsModule {
	@Binds
	fun bindsFeatureFlagsClient(
		featureFlagsClient: OverridableFeatureFlagsClient,
	): FeatureFlagsClient
}