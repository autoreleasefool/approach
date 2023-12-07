package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.AlleyDao
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.AlleyCreate
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.core.model.AlleyUpdate
import ca.josephroque.bowlingcompanion.core.model.utils.sortByUUIDs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class OfflineFirstAlleysRepository @Inject constructor(
	private val alleyDao: AlleyDao,
	private val userDataRepository: UserDataRepository,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): AlleysRepository {
	override fun getRecentAlleysList(limit: Int): Flow<List<AlleyListItem>> =
		combine(
			alleyDao.getAlleysList(),
			userDataRepository.userData.map { it.recentlyUsedAlleyIds }
		) { alleys, recentlyUsed ->
			alleys.sortByUUIDs(recentlyUsed)
				.take(limit)
		}

	override fun getAlleysList(): Flow<List<AlleyListItem>> =
		alleyDao.getAlleysList()

	override fun getAlleyUpdate(id: UUID): Flow<AlleyUpdate> =
		alleyDao.getAlleyUpdate(id).map { it.asModel() }

	override suspend fun insertAlley(alley: AlleyCreate) = withContext(ioDispatcher) {
		alleyDao.insertAlley(alley.asEntity())
	}

	override suspend fun updateAlley(alley: AlleyUpdate) = withContext(ioDispatcher) {
		alleyDao.updateAlley(alley.asEntity())
	}

	override suspend fun deleteAlley(id: UUID) = withContext(ioDispatcher) {
		alleyDao.deleteAlley(id)
	}
}