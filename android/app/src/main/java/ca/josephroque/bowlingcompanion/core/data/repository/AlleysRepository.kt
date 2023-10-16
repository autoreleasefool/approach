package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.model.AlleyCreate
import ca.josephroque.bowlingcompanion.core.database.model.AlleyUpdate
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface AlleysRepository {
	fun getRecentAlleysList(limit: Int): Flow<List<AlleyListItem>>

	suspend fun insertAlley(alley: AlleyCreate)
	suspend fun updateAlley(alley: AlleyUpdate)
	suspend fun deleteAlley(id: UUID)
}