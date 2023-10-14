package ca.josephroque.bowlingcompanion.core.datastore.di

import android.content.Context
import android.preference.PreferenceManager
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import ca.josephroque.bowlingcompanion.core.datastore.UserPreferencesSerializer
import ca.josephroque.bowlingcompanion.core.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.model.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
	@Provides
	@Singleton
	fun providesUserPreferencesDataStore(
		@ApplicationContext context: Context,
		@Dispatcher(ApproachDispatchers.IO) ioDispatcher: CoroutineDispatcher,
		@ApplicationScope scope: CoroutineScope,
		userPreferencesSerializer: UserPreferencesSerializer,
	): DataStore<UserPreferences> =
		DataStoreFactory.create(
			serializer = userPreferencesSerializer,
			scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
			migrations = listOf(
				SharedPreferencesMigration(
					context,
					PreferenceManager.getDefaultSharedPreferencesName(context)
				) { sharedPreferencesView: SharedPreferencesView, userPreferences: UserPreferences ->
					userPreferences.toBuilder()
						.setIsCountingH2AsHDisabled(sharedPreferencesView.getBoolean("pref_count_h2_as_h", false))
						.setIsCountingSplitWithBonusAsSplitDisabled(sharedPreferencesView.getBoolean("pref_count_s2_as_s", false))
						.build()
				}
			),
		) {
			context.dataStoreFile("user_preferences.pb")
		}
}