package ca.josephroque.bowlingcompanion.core.database

import android.content.Context
import androidx.room.Room
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
	): ApproachDatabase = Room.databaseBuilder(
		context,
		ApproachDatabase::class.java,
		"approach-database"
	).build()
}