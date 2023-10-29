package ca.josephroque.bowlingcompanion.core.common.filesystem.di

import ca.josephroque.bowlingcompanion.core.common.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.common.filesystem.SystemFileManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FilesModule {
	@Binds
	fun bindsFileManager(fileManager: SystemFileManager): FileManager
}