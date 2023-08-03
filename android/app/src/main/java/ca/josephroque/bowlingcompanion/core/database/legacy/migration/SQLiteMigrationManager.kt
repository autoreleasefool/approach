package ca.josephroque.bowlingcompanion.core.database.legacy.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ca.josephroque.bowlingcompanion.core.data.repository.LegacyMigrationRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyDatabaseHelper
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.step.migrateBowlers
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.step.migrateGames
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.step.migrateLeagues
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.step.migrateSeries
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.step.migrateTeams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class SQLiteMigrationManager @Inject constructor(
	@ApplicationContext private val context: Context,
	val legacyMigrationRepository: LegacyMigrationRepository,
	private val userDataRepository: UserDataRepository,
	private val transactionRunner: TransactionRunner,
): MigrationManager {

	private val _currentStep = MutableSharedFlow<MigrationStep?>()
	override val currentStep: Flow<MigrationStep?> = _currentStep

	override suspend fun beginMigration() {
		transactionRunner {
			val legacyDb = LegacyDatabaseHelper.getInstance(context).readableDatabase

			for (step in MigrationStep.values()) {
				_currentStep.emit(step)

				run(step, legacyDb)
			}

			LegacyDatabaseHelper.closeInstance()
		}

		legacyMigrationRepository.recordCheckpoint()
		userDataRepository.didCompleteLegacyMigration()
		_currentStep.emit(null)
	}

	private suspend fun run(step: MigrationStep, legacyDb: SQLiteDatabase) {
		when (step) {
			MigrationStep.TEAMS -> migrateTeams(legacyDb)
			MigrationStep.BOWLERS -> migrateBowlers(legacyDb)
			MigrationStep.LEAGUES -> migrateLeagues(legacyDb)
			MigrationStep.SERIES -> migrateSeries(legacyDb)
			MigrationStep.GAMES -> migrateGames(legacyDb)
		}
	}
}