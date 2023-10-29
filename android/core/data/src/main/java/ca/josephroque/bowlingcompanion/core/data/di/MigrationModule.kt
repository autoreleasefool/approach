package ca.josephroque.bowlingcompanion.core.data.di

import ca.josephroque.bowlingcompanion.core.data.migration.MigrationManager
import ca.josephroque.bowlingcompanion.core.data.migration.SQLiteMigrationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MigrationModule {
	@Binds
	fun bindsMigrationManager(migrationManager: SQLiteMigrationManager): MigrationManager
}