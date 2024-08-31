package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedBowler
import ca.josephroque.bowlingcompanion.core.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.BowlerSortOrder
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface BowlersRepository {
	fun getBowlersList(
		sortOrder: BowlerSortOrder = BowlerSortOrder.ALPHABETICAL,
	): Flow<List<BowlerListItem>>
	fun getOpponentsList(): Flow<List<OpponentListItem>>
	fun getArchivedBowlers(): Flow<List<ArchivedBowler>>
	suspend fun getDefaultQuickPlay(): Pair<BowlerSummary, LeagueSummary>?

	fun getBowlerSummary(bowlerId: BowlerID): Flow<BowlerSummary>
	fun getBowlerDetails(bowlerId: BowlerID): Flow<BowlerDetails>
	fun getSeriesBowlers(series: List<UUID>): Flow<List<BowlerSummary>>

	suspend fun insertBowler(bowler: BowlerCreate)
	suspend fun updateBowler(bowler: BowlerUpdate)
	suspend fun archiveBowler(bowlerId: BowlerID)
	suspend fun unarchiveBowler(bowlerId: BowlerID)

	suspend fun hasOpponents(): Boolean
	suspend fun mergeBowlers(bowlers: List<BowlerEntity>, associateBy: Map<BowlerID, BowlerID>)
}
