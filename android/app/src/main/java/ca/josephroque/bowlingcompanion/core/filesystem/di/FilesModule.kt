package ca.josephroque.bowlingcompanion.core.filesystem.di

import ca.josephroque.bowlingcompanion.core.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.filesystem.SystemFileManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FilesModule {
	@Binds
	abstract fun bindsFileManager(fileManager: SystemFileManager): FileManager
}