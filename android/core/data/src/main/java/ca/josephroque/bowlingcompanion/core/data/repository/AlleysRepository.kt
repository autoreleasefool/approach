package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.AlleyCreate
import ca.josephroque.bowlingcompanion.core.model.AlleyDetails
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.core.model.AlleyUpdate
import ca.josephroque.bowlingcompanion.core.model.GameID
import kotlinx.coroutines.flow.Flow

interface AlleysRepository {
	fun getAlleyDetails(id: AlleyID): Flow<AlleyDetails>
	fun getRecentAlleysList(limit: Int): Flow<List<AlleyListItem>>
	fun getAlleysList(): Flow<List<AlleyListItem>>

	fun getAlleyUpdate(id: AlleyID): Flow<AlleyUpdate>

	fun getGameAlleyDetails(gameId: GameID): Flow<AlleyDetails?>

	suspend fun insertAlley(alley: AlleyCreate)
	suspend fun updateAlley(alley: AlleyUpdate)
	suspend fun deleteAlley(id: AlleyID)
}
