package ca.josephroque.bowlingcompanion.core.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import ca.josephroque.bowlingcompanion.core.database.dao.CheckpointDao
import ca.josephroque.bowlingcompanion.core.database.legacy.dao.LegacyIDMappingDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyTeam
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingEntity
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingKey
import ca.josephroque.bowlingcompanion.core.database.model.TeamEntity
import java.util.UUID
import javax.inject.Inject

class OfflineFirstLegacyMigrationRepository @Inject constructor(
	private val teamDao: TeamDao,
	private val legacyIDMappingDao: LegacyIDMappingDao,
	private val checkpointDao: CheckpointDao,
	private val transactionRunner: TransactionRunner,
): LegacyMigrationRepository {

	override suspend fun migrateTeams(teams: List<LegacyTeam>) = transactionRunner {
		val migratedTeams = mutableListOf<TeamEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		teams.forEach { legacyTeam ->
			val id = UUID.randomUUID()
			idMappings.add(
				LegacyIDMappingEntity(
				id = id,
				legacyId = legacyTeam.id,
				key = LegacyIDMappingKey.TEAM
			)
			)

			migratedTeams.add(TeamEntity(
				id = id,
				name = legacyTeam.name,
			))
		}

		legacyIDMappingDao.insertAll(idMappings)
		teamDao.insertAll(migratedTeams)
	}

	override suspend fun recordCheckpoint() {
		checkpointDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
	}
}