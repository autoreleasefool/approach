package ca.josephroque.bowlingcompanion.core.database.legacy.di

import ca.josephroque.bowlingcompanion.core.database.legacy.migration.MigrationManager
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.SQLiteMigrationManager
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