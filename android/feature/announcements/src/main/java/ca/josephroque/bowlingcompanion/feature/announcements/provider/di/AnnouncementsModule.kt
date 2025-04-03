package ca.josephroque.bowlingcompanion.feature.announcements.provider.di

import ca.josephroque.bowlingcompanion.feature.announcements.provider.AnnouncementsProvider
import ca.josephroque.bowlingcompanion.feature.announcements.provider.LiveAnnouncementsProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AnnouncementsModule {
	@Binds
	fun bindAnnouncementsProvider(provider: LiveAnnouncementsProvider): AnnouncementsProvider
}
