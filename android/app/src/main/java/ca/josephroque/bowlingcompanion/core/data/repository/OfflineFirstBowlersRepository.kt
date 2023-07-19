package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.database.model.asExternalModel
import ca.josephroque.bowlingcompanion.core.model.Bowler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class OfflineFirstBowlersRepository @Inject constructor(
	private val bowlerDao: BowlerDao,
): BowlersRepository {
	override fun getBowlers(): Flow<List<Bowler>> =
		bowlerDao.getBowlerEntities()
			.map { it.map(BowlerEntity::asExternalModel) }

	override fun getBowler(id: UUID): Flow<Bowler> =
		bowlerDao.getBowler(id)
			.map(BowlerEntity::asExternalModel)

	override suspend fun upsertBowler(bowler: Bowler) {
		bowlerDao.upsertBowlers(listOf(bowler.asEntity()))
	}

	override suspend fun deleteBowler(id: UUID) {
		bowlerDao.deleteBowler(id)
	}
}