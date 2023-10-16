package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.AlleyDao
import ca.josephroque.bowlingcompanion.core.database.model.AlleyCreate
import ca.josephroque.bowlingcompanion.core.database.model.AlleyUpdate
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.utils.sortByUUIDs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class OfflineFirstAlleysRepository @Inject constructor(
	private val alleyDao: AlleyDao,
	private val userDataRepository: UserDataRepository,
): AlleysRepository {
	override fun getRecentAlleysList(limit: Int): Flow<List<AlleyListItem>> =
		combine(
			alleyDao.getAlleysList(),
			userDataRepository.userData.map { it.recentlyUsedAlleyIds }
		) { alleys, recentlyUsed ->
			alleys.sortByUUIDs(recentlyUsed)
				.subList(0, limit)
		}

	override suspend fun insertAlley(alley: AlleyCreate) {
		alleyDao.insertAlley(alley)
	}

	override suspend fun updateAlley(alley: AlleyUpdate) {
		alleyDao.updateAlley(alley)
	}

	override suspend fun deleteAlley(id: UUID) {
		alleyDao.deleteAlley(id)
	}
}