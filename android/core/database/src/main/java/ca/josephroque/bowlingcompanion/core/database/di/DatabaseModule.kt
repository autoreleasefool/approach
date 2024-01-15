package ca.josephroque.bowlingcompanion.core.database.di

import android.content.Context
import androidx.room.Room
import ca.josephroque.bowlingcompanion.core.database.ApproachDatabase
import ca.josephroque.bowlingcompanion.core.database.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
	@Provides
	@Singleton
	fun providesApproachDatabase(
		@ApplicationContext context: Context,
	): ApproachDatabase = ApproachDatabase.getInstance(context)
}