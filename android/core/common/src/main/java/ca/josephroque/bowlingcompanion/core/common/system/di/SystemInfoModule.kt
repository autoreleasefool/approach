package ca.josephroque.bowlingcompanion.core.common.system.di

import ca.josephroque.bowlingcompanion.core.common.system.LiveSystemInfoService
import ca.josephroque.bowlingcompanion.core.common.system.SystemInfoService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SystemInfoModule {
	@Binds
	fun bindsSystemInfoService(systemInfoService: LiveSystemInfoService): SystemInfoService
}