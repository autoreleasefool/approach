package ca.josephroque.bowlingcompanion.core.data.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.data.migration.step.migrateBowlers
import ca.josephroque.bowlingcompanion.core.data.migration.step.migrateFrames
import ca.josephroque.bowlingcompanion.core.data.migration.step.migrateGames
import ca.josephroque.bowlingcompanion.core.data.migration.step.migrateLeagues
import ca.josephroque.bowlingcompanion.core.data.migration.step.migrateMatchPlays
import ca.josephroque.bowlingcompanion.core.data.migration.step.migrateSeries
import ca.josephroque.bowlingcompanion.core.data.migration.step.migrateTeamBowlers
import ca.josephroque.bowlingcompanion.core.data.migration.step.migrateTeams
import ca.josephroque.bowlingcompanion.core.data.repository.LegacyMigrationRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyDatabaseHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SQLiteMigrationManager @Inject constructor(
	@ApplicationContext private val context: Context,
	val legacyMigrationRepository: LegacyMigrationRepository,
	private val userDataRepository: UserDataRepository,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): MigrationManager {

	private val _currentStep = MutableSharedFlow<MigrationStep?>()
	override val currentStep: Flow<MigrationStep?> = _currentStep

	override suspend fun beginMigration() = withContext(ioDispatcher) {
		if (userDataRepository.userData.first().isLegacyMigrationComplete) return@withContext

		transactionRunner {
			val legacyDb = LegacyDatabaseHelper.getInstance(context).readableDatabase

			for (step in MigrationStep.entries) {
				_currentStep.emit(step)

				run(step, legacyDb)
			}

			LegacyDatabaseHelper.closeInstance()
		}

		userDataRepository.didCompleteLegacyMigration()
		_currentStep.emit(null)
	}

	private suspend fun run(step: MigrationStep, legacyDb: SQLiteDatabase) {
		when (step) {
			MigrationStep.TEAMS -> migrateTeams(legacyDb)
			MigrationStep.BOWLERS -> migrateBowlers(legacyDb)
			MigrationStep.TEAM_BOWLERS -> migrateTeamBowlers(legacyDb)
			MigrationStep.LEAGUES -> migrateLeagues(legacyDb)
			MigrationStep.SERIES -> migrateSeries(legacyDb)
			MigrationStep.GAMES -> migrateGames(legacyDb)
			MigrationStep.MATCH_PLAYS -> migrateMatchPlays(legacyDb)
			MigrationStep.FRAMES -> migrateFrames(legacyDb)
		}
	}
}