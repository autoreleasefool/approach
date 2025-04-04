package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.GearDao
import ca.josephroque.bowlingcompanion.core.database.model.BowlerPreferredGearCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.GameGearCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GearCreate
import ca.josephroque.bowlingcompanion.core.model.GearDetails
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.GearUpdate
import ca.josephroque.bowlingcompanion.core.model.utils.sortByUUIDs
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OfflineFirstGearRepository @Inject constructor(
	private val gearDao: GearDao,
	private val userDataRepository: UserDataRepository,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : GearRepository {
	override fun getBowlerPreferredGear(bowlerId: BowlerID): Flow<List<GearListItem>> =
		gearDao.getBowlerPreferredGear(bowlerId)

	override fun getGameGear(gameId: GameID): Flow<List<GearListItem>> = gearDao.getGameGear(gameId)

	override fun getRecentlyUsedGear(kind: GearKind?, limit: Int): Flow<List<GearListItem>> = combine(
		gearDao.getGearList(kind),
		userDataRepository.userData.map { it.recentlyUsedGearIds },
	) { gear, recentlyUsed ->
		gear.sortByUUIDs(recentlyUsed)
			.take(limit)
	}

	override fun getGearList(kind: GearKind?): Flow<List<GearListItem>> = gearDao.getGearList(kind)

	override fun getGearUpdate(id: GearID): Flow<GearUpdate> = gearDao.getGearUpdate(id).map { it.asModel() }

	override fun getGearDetails(id: GearID): Flow<GearDetails> = gearDao.getGearDetails(id)

	override suspend fun setBowlerPreferredGear(bowlerId: BowlerID, gear: Set<GearID>) = withContext(
		ioDispatcher,
	) {
		gearDao.removeBowlerPreferredGear(bowlerId)
		gearDao.setBowlerPreferredGear(gear.map { BowlerPreferredGearCrossRef(bowlerId, it) })
	}

	override suspend fun setGameGear(gameId: GameID, gear: Set<GearID>) = withContext(ioDispatcher) {
		gearDao.removeGameGear(gameId)
		gearDao.setGameGear(gear.map { GameGearCrossRef(gameId, it) })
	}

	override suspend fun insertGear(gear: GearCreate) = withContext(ioDispatcher) {
		gearDao.insertGear(gear.asEntity())
	}

	override suspend fun updateGear(gear: GearUpdate) = withContext(ioDispatcher) {
		gearDao.updateGear(gear.asEntity())
	}

	override suspend fun deleteGear(id: GearID) = withContext(ioDispatcher) {
		gearDao.deleteGear(id)
	}
}
