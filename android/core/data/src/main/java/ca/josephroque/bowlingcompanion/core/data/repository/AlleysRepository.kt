package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.AlleyCreate
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.core.model.AlleyUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface AlleysRepository {
	fun getRecentAlleysList(limit: Int): Flow<List<AlleyListItem>>
	fun getAlleysList(): Flow<List<AlleyListItem>>

	fun getAlleyUpdate(id: UUID): Flow<AlleyUpdate>

	suspend fun insertAlley(alley: AlleyCreate)
	suspend fun updateAlley(alley: AlleyUpdate)
	suspend fun deleteAlley(id: UUID)
}