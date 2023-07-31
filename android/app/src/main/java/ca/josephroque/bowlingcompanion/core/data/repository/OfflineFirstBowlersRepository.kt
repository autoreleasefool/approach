package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.database.model.asExternalModel
import ca.josephroque.bowlingcompanion.core.model.Bowler
import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class OfflineFirstBowlersRepository @Inject constructor(
	private val bowlerDao: BowlerDao,
): BowlersRepository {
	override fun getBowlers(query: BowlerQuery): Flow<List<Bowler>> =
		bowlerDao.getBowlers(kind = query.kind)
			.map { it.map(BowlerEntity::asExternalModel) }

	override fun getBowler(id: UUID): Flow<Bowler> =
		bowlerDao.getBowler(id)
			.map(BowlerEntity::asExternalModel)

	override suspend fun insertBowler(bowler: Bowler) {
		bowlerDao.insert(bowler.asEntity())
	}

	override suspend fun updateBowler(bowler: BowlerUpdate) {
		bowlerDao.updateBowler(bowler)
	}

	override suspend fun deleteBowler(id: UUID) {
		bowlerDao.deleteBowler(id)
	}
}